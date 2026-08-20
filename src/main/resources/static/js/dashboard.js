document.addEventListener("DOMContentLoaded", () => {
    const foodCards = document.querySelectorAll(".food-card");
    const consumeButton = document.getElementById("consumeButton");
    const selectedCountSpan = document.getElementById("selectedCount");

    const modalOverlay = document.getElementById("modalOverlay");
    const modalMessage = document.getElementById("modalMessage");
    const cancelBtn = document.getElementById("cancelBtn");
    const confirmBtn = document.getElementById("confirmBtn");

    if (!foodCards.length || !consumeButton || !selectedCountSpan) return;

    // 1. カード選択（色変更 & 件数カウント）
    foodCards.forEach(card => {
        card.addEventListener("click", () => {
            card.classList.toggle("selected");
            updateConsumeButton();
        });
    });

    // 2. 「消費済みにする」ボタンの表示制御
    function updateConsumeButton() {
        const selectedCards = document.querySelectorAll(".food-card.selected");
        const count = selectedCards.length;

        if (count > 0) {
            selectedCountSpan.textContent = count;
            consumeButton.classList.add("show");
        } else {
            consumeButton.classList.remove("show");
        }
    }

    // 3. 消費ボタンクリックでポップアップ表示
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

    // 4. モーダルのキャンセルボタン
    if (cancelBtn) {
        cancelBtn.addEventListener("click", closeModal);
    }

    // 5. モーダルの「消費する」ボタン（実際にカードを削除＆数値更新）
    if (confirmBtn) {
        confirmBtn.addEventListener("click", () => {
            const selectedCards = document.querySelectorAll(".food-card.selected");
            selectedCards.forEach(card => card.remove());

            closeModal();
            updateConsumeButton();
            updateSummary();
        });
    }

    // 6. モーダルの背景クリックで閉じる
    if (modalOverlay) {
        modalOverlay.addEventListener("click", (e) => {
            if (e.target === modalOverlay) closeModal();
        });
    }

    function closeModal() {
        if (modalOverlay) modalOverlay.classList.remove("show");
    }

    // 7. サマリーの数値を自動再計算
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