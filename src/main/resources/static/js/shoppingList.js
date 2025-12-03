// shoppingList.js

// ------------------------------
// モーダル操作
// ------------------------------
function openModal(shoppingListId, itemName) {
    document.getElementById("shoppingListId").value = shoppingListId;
    document.getElementById("itemName").value = itemName;

    const modal = document.getElementById("purchasedModal");
    modal.style.display = "block";   // ← これが絶対必要
    modal.classList.add("show");
}

function closeModal() {
    const modal = document.getElementById("purchasedModal");
    modal.style.display = "none";    // ← 非表示
    modal.classList.remove("show");
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
    const favorite = document.getElementById("favorite").checked;
    const categoryId = document.getElementById("categoryId").value;

    if (!categoryId) {
        alert("カテゴリーを選択してください");
        return;
    }

    const data = { id, name, amount, deadline, others, favorite, categoryId };

	fetch('/users/add-to-shopping-list', {
	    method: 'POST',
	    headers: { 'Content-Type': 'application/json' },
	    body: JSON.stringify(data)
	})
	.then(response => {
	    if (!response.ok) throw new Error('Network response was not ok');
	    return response.json(); // または response.text() でもOK
	})
	.then(item => {
	    // 追加成功後に JS でリダイレクト
	    window.location.href = '/users/shoppingList';
	})
	.catch(error => {
	    console.error('Error:', error);
	    const msgDiv = document.getElementById("message");
	    msgDiv.style.color = "red";
	    msgDiv.textContent = "失敗しました";
	});

}

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





















