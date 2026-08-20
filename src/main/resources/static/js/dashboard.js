document.addEventListener("DOMContentLoaded", () => {
    const foodCards = document.querySelectorAll(".food-card");
    const consumeButton = document.getElementById("consumeButton");
    const selectedCountSpan = document.getElementById("selectedCount");

    if (!foodCards.length || !consumeButton || !selectedCountSpan) {
        return;
    }

    foodCards.forEach(card => {
        card.addEventListener("click", () => {
            card.classList.toggle("selected");
            updateConsumeButton();
        });
    });

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

    consumeButton.addEventListener("click", () => {
        const selectedCards = document.querySelectorAll(".food-card.selected");
        if (selectedCards.length === 0) return;

        if (confirm(`${selectedCards.length}件のアイテムを消費済みにしますか？`)) {
            selectedCards.forEach(card => card.remove());
            updateConsumeButton();
            updateSummary();
        }
    });

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