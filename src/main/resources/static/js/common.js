/**
 * 共通UI処理
 */
document.addEventListener("DOMContentLoaded", () => {

    /*
     * モーダルを閉じる
     *
     * .modal-overlay を使用している
     * 全画面で利用可能
     */
    const modalOverlays =
        document.querySelectorAll(".modal-overlay");

    modalOverlays.forEach(modal => {

        modal.addEventListener("click", (event) => {

            // モーダル本体をクリックした場合は閉じない
            if (event.target !== modal) {
                return;
            }

            modal.classList.remove("show");
        });

    });


    /*
     * ESCキーでモーダルを閉じる
     */
    document.addEventListener("keydown", (event) => {

        if (event.key !== "Escape") {
            return;
        }

        document
            .querySelectorAll(".modal-overlay.show")
            .forEach(modal => {

                modal.classList.remove("show");

            });

    });

});