document.addEventListener("DOMContentLoaded", () => {

    const receiptMethodButton =
        document.getElementById("receiptMethodButton");

    const receiptModal =
        document.getElementById("receiptModal");

    const closeReceiptModal =
        document.getElementById("closeReceiptModal");


    /*
     * レシート読み取りカードをクリック
     */
    if (receiptMethodButton && receiptModal) {

        receiptMethodButton.addEventListener("click", () => {

            receiptModal.classList.add("show");

        });

    }


    /*
     * 閉じるボタン
     */
    if (closeReceiptModal && receiptModal) {

        closeReceiptModal.addEventListener("click", () => {

            receiptModal.classList.remove("show");

        });

    }

});