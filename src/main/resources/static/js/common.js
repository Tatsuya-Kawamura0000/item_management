function showPopup(message, type = "success") {

    let popup = document.getElementById("popupMessage");

    if (!popup) {
        popup = document.createElement("span");
        popup.id = "popupMessage";

        // bodyの最後に追加（どの画面でも安定）
        document.body.appendChild(popup);
    }

    popup.className = "popup-message popup-" + type;
    popup.textContent = message;

    popup.classList.add("show");

    setTimeout(() => {
        popup.classList.remove("show");
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