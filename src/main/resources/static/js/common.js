
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
