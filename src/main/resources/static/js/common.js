function showPopup(message, type = "success", targetSelector = "body") {

    let popup = document.getElementById("popupMessage");

    if (!popup) {
        popup = document.createElement("span");
        popup.id = "popupMessage";

        const target = document.querySelector(targetSelector);
        target.appendChild(popup);
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