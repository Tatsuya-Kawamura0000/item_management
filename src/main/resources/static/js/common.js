/**
 * 共通UI処理
 */
document.addEventListener("DOMContentLoaded", () => {

    /*
     * モーダルを閉じる
     *
     * 以下のモーダルを共通で処理する
     * ・.modal-overlay
     * ・.shopping-modal-overlay
     *
     * モーダル本体をクリックした場合は閉じず、
     * 背景部分をクリックした場合のみ閉じる。
     */
    const modalOverlays =
        document.querySelectorAll(
            ".modal-overlay, .shopping-modal-overlay"
        );


    modalOverlays.forEach(modal => {

        modal.addEventListener("click", (event) => {

            /*
             * モーダル本体をクリックした場合は
             * 閉じない。
             */
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


        /*
         * 既存モーダル
         */
        document
            .querySelectorAll(".modal-overlay.show")
            .forEach(modal => {

                modal.classList.remove("show");

            });


        /*
         * 買い物リスト用モーダル
         */
        document
            .querySelectorAll(".shopping-modal-overlay.show")
            .forEach(modal => {

                modal.classList.remove("show");

            });

    });

});