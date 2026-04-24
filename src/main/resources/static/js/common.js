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



//ポップアップメッセージ　
function showPopupAndReload(message, type = "success") {
    // 1. ポップアップ要素を作成
    const popup = document.createElement('div');
    popup.className = `custom-modal-popup ${type}`;

    // 2. アイコンの判定（FontAwesomeを使用している前提）
    const icon = type === "success" ? "fa-circle-check" : "fa-circle-exclamation";

    // 3. 中身の構築
    popup.innerHTML = `
        <div class="popup-content">
            <i class="fa-solid ${icon}"></i>
            <span>${message}</span>
        </div>
    `;

    // 4. bodyに直接追加（これでレイアウト崩れを防ぐ）
    document.body.appendChild(popup);

    // 5. 3秒後にリロード
    setTimeout(() => {
        location.reload();
    }, 3000);
}
