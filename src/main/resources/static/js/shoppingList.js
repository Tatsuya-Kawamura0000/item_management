// shoppingList.js

// ------------------------------
// モーダル操作
// ------------------------------
function openModal(id, name) {
    const modal = document.getElementById("purchasedModal");

    document.getElementById("shoppingListId").value = id;
    document.getElementById("itemName").value = name;

    modal.classList.add("show");
}

function closeModal() {
    document.getElementById("purchasedModal")
        .classList.remove("show");
}


// ------------------------------
// 食材一覧に追加
// ------------------------------
function submitPurchased() {
    const id = document.getElementById("shoppingListId").value;
    const name = document.getElementById("itemName").value;
    const amount = document.getElementById("amount").value;
    const deadline = document.getElementById("deadline").value;
    const others = document.getElementById("others").value;

    // ★ hidden を読む（0 / 1）
    const favorite = document.getElementById("modalFavoriteField").value;

    const categoryId = document.getElementById("categoryId").value;

    if (!categoryId) {
        alert("カテゴリーを選択してください");
        return;
    }

    const data = { id, name, amount, deadline, others, favorite: favorite === "1", categoryId };

	fetch('/users/add-to-shopping-list', {
	    method: 'POST',
	    headers: { 'Content-Type': 'application/json' },
	    body: JSON.stringify(data)
	})
	.then(async response => {
	    const resJson = await response.json();
	    if (!response.ok) {
	        // 文字数オーバーの場合
	        if(resJson.error === "length") {
	            throw new Error("量は20文字以内,メモは100文字以内で入力してください");
	        } else {
	            throw new Error("登録に失敗しました 　※文字数オーバーしていませんか？");
	        }
	    }
	    return resJson;
	})
	.then(item => {
	    window.location.href = '/users/shoppingList';
	})
	.catch(error => {
	    console.error('Error:', error);
	    alert(error.message);
	});

}


$(function () {

    const modalHeartBtn = $('#modalFavoriteButton i');
    const modalField = $('#modalFavoriteField');

    // 初期色（0ならグレー、1ならピンク）
    modalHeartBtn.css('color', modalField.val() === "1" ? '#d81b60' : 'gray');

    // クリックで切り替え
    $('#modalFavoriteButton').click(function () {
        let fav = modalField.val() === "1" ? "0" : "1";  // 0 ⇄ 1
        modalField.val(fav);
        modalHeartBtn.css('color', fav === "1" ? '#d81b60' : 'gray');
    });

});

// ------------------------------
// 食材一覧へ戻る
// ------------------------------
function goBack() {
    window.location.href = '/users';
}

// ------------------------------
// ポップアップ自動消去
// ------------------------------
window.addEventListener('DOMContentLoaded', function() {
    const popup = document.getElementById('popupMessage');
    if (popup) {
        setTimeout(() => {
            popup.style.transition = "opacity 0.5s ease-out";
            popup.style.opacity = '0';
            setTimeout(() => popup.remove(), 500);
        }, 2500);
    }
});

// ------------------------------
// チェックボックス関連
// ------------------------------
function checkAll() {
    document.querySelectorAll('input[name="selectedItems"]').forEach(cb => {
        cb.checked = true;
        highlightRow(cb);
    });
}

function uncheckAll() {
    document.querySelectorAll('input[name="selectedItems"]').forEach(cb => {
        cb.checked = false;
        highlightRow(cb);
    });
}

function highlightRow(checkbox) {
    const row = checkbox.closest('tr');
    if (checkbox.checked) {
        row.classList.add('selected-row');
    } else {
        row.classList.remove('selected-row');
    }
}

// ------------------------------
// 新規追加モーダル操作（★グローバルスコープに出す）
// ------------------------------
function openNewItemModal() {
    document.getElementById("newItemName").value = "";
    document.getElementById("newItemAmount").value = "";
    const modal = document.getElementById("newItemModal");
    modal.style.display = "block";
}

function closeNewItemModal() {
    const modal = document.getElementById("newItemModal");
    modal.style.display = "none";
}

function submitNewItem() {
    const name = document.getElementById("newItemName").value;
    const amount = document.getElementById("newItemAmount").value;

    if (!name || !amount) {
        alert("食材名と量を入力してください");
        return;
    }

    // ★ 文字数チェック
    if (name.length > 50) {
        alert("購入するものは50文字以内で入力してください");
        return;
    }

    if (amount.length > 20) {
        alert("量は20文字以内で入力してください");
        return;
    }

    const form = document.createElement("form");
    form.method = "POST";
    form.action = "/users/add-new-item-to-shopping-list";

    form.innerHTML = `
        <input type="hidden" name="name" value="${name}">
        <input type="hidden" name="amount" value="${amount}">
    `;

    document.body.appendChild(form);
    form.submit();
}





// ------------------------------
// DOM読み込み後にやること
// ------------------------------
window.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('input[name="selectedItems"]').forEach(cb => {
        cb.addEventListener('change', () => highlightRow(cb));
        highlightRow(cb);
    });
});





















