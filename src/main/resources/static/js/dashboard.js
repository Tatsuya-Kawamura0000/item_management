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
    const foodCards = document.querySelectorAll(".food-card");
    const consumeButton = document.getElementById("consumeButton");
    const selectedCountSpan = document.getElementById("selectedCount");

    const modalOverlay = document.getElementById("modalOverlay");
    const modalMessage = document.getElementById("modalMessage");
    const cancelBtn = document.getElementById("cancelBtn");
    const confirmBtn = document.getElementById("confirmBtn");

    if (!foodCards.length || !consumeButton || !selectedCountSpan) return;

    function updateConsumeButton() {
        const selectedCards = document.querySelectorAll(".food-card.selected");
        const count = selectedCards.length;

        if (count > 0) {
            selectedCountSpan.textContent = count;
            consumeButton.classList.add("show");
        } else {
            selectedCountSpan.textContent = "0";
            consumeButton.classList.remove("show");
        }
    }

    foodCards.forEach(card => {
        card.addEventListener("click", () => {
            card.classList.toggle("selected");
            updateConsumeButton();
        });
    });

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
                updateSummary();
                updateConsumeButton();
                setTimeout(() => {
                    window.location.reload();
                }, 1500);
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

    function closeModal() {
        if (modalOverlay) modalOverlay.classList.remove("show");
    }

    function updateSummary() {
        const soonCount = document.querySelectorAll("#soonList .food-card").length;
        const expiredCount = document.querySelectorAll("#expiredList .food-card").length;

        const soonCountEl = document.getElementById("soonCount");
        const expiredCountEl = document.getElementById("expiredCount");
        const totalCountEl = document.getElementById("totalCount");

        if (soonCountEl) soonCountEl.textContent = soonCount;
        if (expiredCountEl) expiredCountEl.textContent = expiredCount;
        if (totalCountEl) totalCountEl.textContent = soonCount + expiredCount;
    }
});