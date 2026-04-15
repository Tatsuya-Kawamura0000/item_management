function showPopup(message, type = "success", targetSelector = ".bulk-actions") {

    const target = document.querySelector(targetSelector);

    // ★ 毎回その場所専用のpopupを作る
    const popup = document.createElement("span");
    popup.className = "popup-message popup-" + type;
    popup.textContent = message;

    target.appendChild(popup);

    // 表示
    setTimeout(() => {
        popup.classList.add("show");
    }, 10);

    // フェードアウト
    setTimeout(() => {
        popup.classList.remove("show");

        // ★ 消す（これ重要）
        setTimeout(() => popup.remove(), 500);
    }, 3000);
}

function showModalMessage(message, type = "success") {

    const el = document.getElementById("modalMessage");

    el.textContent = message;
    el.className = "modal-message show " + type;

    setTimeout(() => {
        el.classList.remove("show");
    }, 2000);
}

//食材一覧　用ポップアップ目メッセージ　　買い物リストへ追加　ボタンの右隣に表示

function showPopupAndReload(message, type = "success", targetSelector = ".bulk-actions") {
    showPopup(message, type, targetSelector);

    setTimeout(() => {
        location.reload();
    }, 3000);
}