function showGlobalToast(message, type = "success") {
    let toast = document.getElementById("globalToast");

    if (!toast) {
        toast = document.createElement("div");
        toast.id = "globalToast";
        toast.className = "custom-modal-popup";
        document.body.appendChild(toast);
    }

    toast.className = `custom-modal-popup ${type}`;
    toast.innerHTML = `
        <div class="popup-content">
            <i class="fa-solid ${type === "success" ? "fa-circle-check" : "fa-circle-exclamation"}"></i>
            <span>${message}</span>
        </div>
    `;

    toast.style.display = "block";
    clearTimeout(window.__toastTimeoutId);
    window.__toastTimeoutId = setTimeout(() => {
        toast.style.display = "none";
    }, 1500);
}

async function bulkConsumeSelectedItems(ids) {
    const response = await fetch("/items/bulk-stop", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(ids)
    });

    if (!response.ok) {
        throw new Error("消費済み更新に失敗しました");
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const consumeButton = document.getElementById("consumeButton");
    const selectedCountSpan = document.getElementById("selectedCount");
    const modalOverlay = document.getElementById("modalOverlay");
    const modalMessage = document.getElementById("modalMessage");
    const cancelBtn = document.getElementById("cancelBtn");
    const confirmBtn = document.getElementById("confirmBtn");

    function parseDateFromText(dateElem) {
        if (!dateElem) return new Date(0);
        const txt = (dateElem.textContent || '').trim();
        const parts = txt.split('/');
        if (parts.length >= 2) {
            const month = parseInt(parts[0], 10) - 1;
            const day = parseInt(parts[1], 10);
            const now = new Date();
            return new Date(now.getFullYear(), month, day);
        }
        return new Date(0);
    }

    // Sort cards by deadline (earliest first) and show empty state if none
    function sortAndHandleEmpty(containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;

        // remove pre-existing empty placeholder
        const existingEmpty = container.querySelector('.empty-card');
        if (existingEmpty) existingEmpty.remove();

        const cards = Array.from(container.querySelectorAll('.food-card'));
        if (!cards.length) {
            const el = document.createElement('div');
            el.className = 'empty-card';
            el.textContent = '該当なし';
            container.appendChild(el);
            return;
        }

        cards.sort((a, b) => {
            const aD = a.dataset.deadline ? new Date(a.dataset.deadline) : parseDateFromText(a.querySelector('.food-card-date'));
            const bD = b.dataset.deadline ? new Date(b.dataset.deadline) : parseDateFromText(b.querySelector('.food-card-date'));
            return aD - bD;
        });

        cards.forEach(c => container.appendChild(c));
    }

    function updateSummary(consumedCount = 0) {
        const soonCount = document.querySelectorAll("#soonList .food-card").length;
        const expiredCount = document.querySelectorAll("#expiredList .food-card").length;

        const soonCountEl = document.getElementById("soonCount");
        const expiredCountEl = document.getElementById("expiredCount");
        const totalCountEl = document.getElementById("totalCount");

        if (soonCountEl) soonCountEl.textContent = soonCount;
        if (expiredCountEl) expiredCountEl.textContent = expiredCount;
        if (totalCountEl && consumedCount > 0) {
            const currentTotal = parseInt(totalCountEl.textContent || "0", 10);
            totalCountEl.textContent = Math.max(0, currentTotal - consumedCount);
        }
    }

    function updateConsumeButton() {
        const selectedCards = document.querySelectorAll(".food-card.selected");
        const count = selectedCards.length;

        if (selectedCountSpan) {
            selectedCountSpan.textContent = count > 0 ? count : "0";
        }
        if (consumeButton) {
            if (count > 0) {
                consumeButton.classList.add("show");
            } else {
                consumeButton.classList.remove("show");
            }
        }
    }

    function attachCardEvents() {
        const foodCards = document.querySelectorAll(".food-card");
        foodCards.forEach(card => {
            card.addEventListener("click", () => {
                card.classList.toggle("selected");
                updateConsumeButton();
            });
        });
    }

    // Initialize list sorting and empty state
    sortAndHandleEmpty('soonList');
    sortAndHandleEmpty('expiredList');
    attachCardEvents();

    if (consumeButton) {
        consumeButton.addEventListener("click", () => {
            const selectedCards = document.querySelectorAll(".food-card.selected");
            if (selectedCards.length === 0) return;

            if (modalMessage) {
                modalMessage.textContent = `選択した ${selectedCards.length} 件の食材を消費済みにしますか？`;
            }
            if (modalOverlay) {
                modalOverlay.classList.add("show");
            }
        });
    }

    function closeModal() {
        if (modalOverlay) modalOverlay.classList.remove("show");
    }

    if (cancelBtn) {
        cancelBtn.addEventListener("click", closeModal);
    }

    if (confirmBtn) {
        confirmBtn.addEventListener("click", async () => {
            const selectedCards = document.querySelectorAll(".food-card.selected");
            if (!selectedCards.length) {
                closeModal();
                return;
            }

            const ids = Array.from(selectedCards)
                .map(card => Number(card.dataset.id))
                .filter(id => Number.isFinite(id));

            if (!ids.length) {
                showGlobalToast("対象の食材IDが取得できませんでした", "error");
                closeModal();
                return;
            }

            try {
                await bulkConsumeSelectedItems(ids);
                closeModal();
                showGlobalToast("消費済みにしました");
                selectedCards.forEach(card => card.remove());
                sortAndHandleEmpty('soonList');
                sortAndHandleEmpty('expiredList');
                updateSummary(ids.length);
                updateConsumeButton();
            } catch (error) {
                console.error(error);
                closeModal();
                showGlobalToast("消費済みの更新に失敗しました", "error");
            }
        });
    }

    if (modalOverlay) {
        modalOverlay.addEventListener("click", (e) => {
            if (e.target === modalOverlay) closeModal();
        });
    }
});