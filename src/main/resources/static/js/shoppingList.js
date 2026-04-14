

// shoppingList.js

let purchaseQueue = [];
let currentIndex = 0;

// ------------------------------
// モーダル操作
// ------------------------------
function openModal(id, name) {
    const modal = document.getElementById("purchasedModal");

    document.getElementById("shoppingListId").value = id;
    document.getElementById("itemName").value = name;

    modal.classList.add("show")
}

function closeModal() {
    document.getElementById("purchasedModal")
        .classList.remove("show");
}


// ------------------------------
// 食材一覧に追加
// ------------------------------
function submitPurchased() {

    // デバッグ用
    console.log("submitPurchased called");

    const id = document.getElementById("shoppingListId").value;
    const name = document.getElementById("itemName").value;
    const amount = document.getElementById("amount").value;
    const deadline = document.getElementById("deadline").value;
    const others = document.getElementById("others").value;
    const categoryId = document.getElementById("categoryId").value;
    const favorite = document.getElementById("modalFavoriteField").value;

    if (!categoryId) {
        showModalMessage("カテゴリーを選択してください", "error");
        return;
    }

    const data = { id, name, amount, deadline, others, categoryId ,favorite};

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

            showModalMessage("追加しました", "success");

            currentIndex++;

            if(currentIndex < purchaseQueue.length){

                openPurchaseModal();

            }else{

                showModalMessage("すべて食材一覧に追加しました", "success");

                setTimeout(() => {

                    closeModal();

                    window.location.href = '/users/shoppingList';

                }, 2000); // 2秒後、モーダルを閉じる
            }

        })

        .catch(error => {
            console.error(error);

            showModalMessage(error.message, "error");
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

    if (!name) {
        showNewItemModalMessage("食材名を入力してください", "error");
        return;
    }

    if (name.length > 50) {
        showNewItemModalMessage("購入するものは50文字以内で入力してください", "error");
        return;
    }

    if (amount.length > 20) {
        showNewItemModalMessage("量は20文字以内で入力してください", "error");
        return;
    }

    fetch("/users/add-to-shopping-list", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            name: name,
            amount: amount
        })
    })
        .then(response => response.json())
        .then(item => {

            // ★ 追加したデータをテーブルに反映
            addRowToTable(item);
            showNewItemModalMessage("追加しました","success");

            // 入力クリア
            document.getElementById("newItemName").value = "";
            document.getElementById("newItemAmount").value = "";

            // フォーカス戻す
            document.getElementById("newItemName").focus();
        })
    .catch(error => {
        console.error(error);
        showNewItemModalMessage("登録に失敗しました","error");
    });
}

function showNewItemModalMessage(message, type = "success") {

    const el = document.getElementById("newItemModalMessage");

    el.textContent = message;
    el.className = "modal-message show " + type;

    setTimeout(() => {
        el.classList.remove("show");
    }, 2000);
}

function addRowToTable(item) {

    const table = document.getElementById("shoppingListTable");

    // 最後の行に追加
    const row = table.insertRow(-1);

    // チェックボックス
    const cell1 = row.insertCell(0);
    cell1.innerHTML = `
        <input type="checkbox"
            class="shopping-checkbox"
            value="${item.id}"
            data-name="${item.name}"
            data-category-id="${item.categoryId}"
            data-favorite="${item.favorite}">
    `;

    // 食材名
    const cell2 = row.insertCell(1);
    cell2.textContent = item.name;

    // 量
    const cell3 = row.insertCell(2);
    cell3.textContent = item.amount;

    // hidden列（カテゴリ）
    const cell4 = row.insertCell(3);
    cell4.classList.add("hidden");
    cell4.textContent = item.categoryName || "";

    // hidden列（購入日）
    const cell5 = row.insertCell(4);
    cell5.classList.add("hidden");

    // hidden列（購入済み）
    const cell6 = row.insertCell(5);
    cell6.classList.add("hidden");

    // 空セル
    const cell7 = row.insertCell(6);
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


// ------------------------------
// 全選択操作
// ------------------------------


document.getElementById("selectAllShopping").addEventListener("change", function(){

    const checked = this.checked;

    const checkboxes = document.querySelectorAll(".shopping-checkbox");

    checkboxes.forEach(cb=>{
        cb.checked = checked;
    });

});

function getSelectedShoppingItems(){

    const checkboxes = document.querySelectorAll(".shopping-checkbox:checked");

    const items = [];

    checkboxes.forEach(cb => {

        items.push({
            id: cb.value,
            name: cb.dataset.name,
            categoryId: cb.dataset.categoryId,
            favorite: cb.dataset.favorite
        });

    });

    return items;
}

document.getElementById("bulkPurchaseBtn").addEventListener("click", function(){

    purchaseQueue = getSelectedShoppingItems();

    if(purchaseQueue.length === 0){
        alert("選択してください");
        return;
    }

    currentIndex = 0;

    openPurchaseModal();

});

	
	
function openPurchaseModal(){

    const item = purchaseQueue[currentIndex];

    console.log(item);  // ←追加 デバック用

    document.getElementById("shoppingListId").value = item.id;
    document.getElementById("itemName").value = item.name;
    document.getElementById("categoryId").value = item.categoryId;
    document.getElementById("purchasedModal").classList.add("show");
    document.getElementById("modalFavoriteField").value = item.favorite;
}

document.getElementById("bulkDeleteBtn").addEventListener("click", function(){

    const items = getSelectedShoppingItems();

    if(items.length === 0){
        alert("選択してください");
        return;
    }

    const ids = items.map(i => i.id);

    fetch("/users/bulk-delete-shopping-list",{
        method:"POST",
        headers:{
            "Content-Type":"application/json"
        },
        body:JSON.stringify(ids)
    })
    .then(()=>{
        location.reload();
    })
    .catch(error=>{
        console.error(error);
        alert("削除に失敗しました");
    });

});







