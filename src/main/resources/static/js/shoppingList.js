/**
 * 買い物リスト画面用 JavaScript
 *
 * このファイルでは、
 * 買い物リスト画面固有の処理を管理する。
 *
 * ・チェック操作
 * ・食材追加
 * ・お気に入り / 食材一覧からの追加
 * ・数量入力
 * ・購入済み処理
 * ・一括削除
 * ・モーダル操作
 */


/* =========================
   State
========================= */

/**
 * 現在開いている選択一覧の種類
 * "favorite"：お気に入り
 * "food"    ：食材一覧
 */
let currentSelectionType = null;

/**
 * 数量入力中の食材
 */
let selectedFood = null;


/* =========================
   Login User
========================= */

const loginUserName = "河村";
const loginUserElement = document.getElementById("loginUserName");

if (loginUserElement) {
    loginUserElement.textContent = loginUserName;
}


/* =========================
   Event Registration
========================= */

document.addEventListener("DOMContentLoaded", () => {

    /*
     * 新規追加モーダル関連
     */
    document.getElementById("openAddModalButton")?.addEventListener("click", openAddModal);
    document.getElementById("closeAddModalButton")?.addEventListener("click", () => closeModal("addModal"));
    document.getElementById("addFoodButton")?.addEventListener("click", addNewFood);

    /*
     * お気に入り・食材一覧モーダル関連
     */
    document.getElementById("openFavoriteModalButton")?.addEventListener("click", () => openSelectionModal("favorite"));
    document.getElementById("openFoodModalButton")?.addEventListener("click", () => openSelectionModal("food"));
    document.getElementById("closeSelectionModalButton")?.addEventListener("click", () => closeModal("selectionModal"));
    document.getElementById("selectionAddButton")?.addEventListener("click", addSelectedFoods);

    /*
     * 数量入力モーダル関連
     */
    document.getElementById("cancelQuantityButton")?.addEventListener("click", cancelQuantityModal);
    document.getElementById("confirmQuantityButton")?.addEventListener("click", confirmQuantity);

    /*
     * 購入確認モーダル関連
     */
    document.getElementById("cancelPurchaseButton")?.addEventListener("click", () => closeModal("purchaseModal"));
    document.getElementById("completePurchaseButton")?.addEventListener("click", completePurchase);
    document.getElementById("purchaseButton")?.addEventListener("click", purchaseSelected);

    /*
     * 削除処理 (API連携)
     */
    document.getElementById("deleteButton")?.addEventListener("click", handleDeleteSelected);

    /*
     * 初期表示処理
     */
    registerCheckEvents();
    updateCount();
});


/* =========================
   Delete API
========================= */

/**
 * 選択された買い物リストアイテムを一括削除する
 */
async function handleDeleteSelected() {
    const checkedItems = document.querySelectorAll('.shopping-item.checked');

    if (checkedItems.length === 0) {
        alert('削除する項目を選択してください。');
        return;
    }

    if (!confirm('選択した項目を買い物リストから削除しますか？')) {
        return;
    }

    const selectedIds = Array.from(checkedItems).map(item => parseInt(item.getAttribute('data-id'), 10));

    try {
        const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
        const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

        const headers = {
            'Content-Type': 'application/json'
        };
        if (token && header) {
            headers[header] = token;
        }

        const response = await fetch('/shoppingList/bulk-delete', {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(selectedIds)
        });

        if (response.ok) {
            checkedItems.forEach(item => item.remove());
            updateCount();
        } else {
            alert('削除処理に失敗しました。');
        }
    } catch (error) {
        console.error('Error deleting items:', error);
        alert('通信エラーが発生しました。');
    }
}


/* =========================
   Check
========================= */

/**
 * 買い物リストのチェック状態を変更する。
 */
function toggleCheck(button) {
    const item = button.closest(".shopping-item");
    if (!item) {
        return;
    }
    item.classList.toggle("checked");
}

/**
 * 買い物リスト内のチェックボタンにクリックイベントを設定する。
 */
function registerCheckEvents() {
    const buttons = document.querySelectorAll(".shopping-check-button");
    buttons.forEach(button => {
        button.addEventListener("click", () => toggleCheck(button));
    });
}


/* =========================
   Count
========================= */

/**
 * 現在の買い物リスト件数を更新する。
 */
function updateCount() {
    const items = document.querySelectorAll(".shopping-item");
    const count = items.length;
    const itemCount = document.getElementById("itemCount");

    if (itemCount) {
        itemCount.textContent = count;
    }
}


/* =========================
   New Food
========================= */

function openAddModal() {
    document.getElementById("addModal")?.classList.add("show");
}

async function addNewFood() {
    const name = document.getElementById("newFoodName")?.value.trim();
    const quantity = document.getElementById("newFoodQuantity")?.value.trim();
    const unit = document.getElementById("newFoodUnit")?.value;
    const categoryId = document.getElementById("newFoodCategory")?.value;

    if (!name || !quantity) {
        alert("食材名と量を入力してください。");
        return;
    }

    const displayQuantity = `${quantity}${unit || ""}`;

    try {
        const response = await fetch("/shopping-list/add-new", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                itemId: Number(selectedFood.id),
                name: name,
                amount: displayQuantity,
                categoryId: Number(categoryId)
            })
        });

        if (!response.ok) {
            throw new Error("買い物リストへの登録に失敗しました。");
        }

        const savedItem = await response.json();

        addShoppingItem(savedItem.id, name, displayQuantity, categoryId);

        document.getElementById("newFoodName").value = "";
        document.getElementById("newFoodQuantity").value = "1";
        closeModal("addModal");

        updateCount();

    } catch (error) {
        console.error(error);
        alert("食材の登録に失敗しました。");
    }
}


/* =========================
   Shopping Item
========================= */

function addShoppingItem(id, name, quantity, categoryId) {
    const item = document.createElement("div");
    item.className = "shopping-item";

    item.dataset.id = id;
    item.dataset.name = name;
    item.dataset.amount = quantity;
    item.dataset.categoryId = categoryId;

    item.innerHTML = `
        <div class="shopping-item__check">
            <button
                type="button"
                class="shopping-check-button"
                aria-label="${escapeHtml(name)}を購入済みにする">
            </button>
        </div>

        <div class="shopping-item__food">
            <div class="shopping-item__food-name">
                ${escapeHtml(name)}
            </div>
        </div>

        <div class="shopping-item__quantity">
            ${escapeHtml(quantity)}
        </div>
    `;

    document.getElementById("shoppingList")?.appendChild(item);

    const checkButton = item.querySelector(".shopping-check-button");
    checkButton?.addEventListener("click", () => toggleCheck(checkButton));
}


/* =========================
   Food Data & Categories
========================= */

const categoryOrder = [
    "野菜",
    "肉",
    "魚",
    "卵・乳製品",
    "乳製品",
    "パン",
    "その他"
];

function getCategoryClass(category) {
    const categoryClassMap = {
        "肉": "category-meat",
        "魚": "category-fish",
        "野菜": "category-vegetable",
        "その他": "category-other",
        "調味料": "category-seasoning",
        "残り物": "category-leftover",
        "飲み物": "category-drink",
        "保存食": "category-preserved"
    };

    return categoryClassMap[category] || "category-other";
}

function parsePurchaseAmount(value) {
    if (!value) {
        return {
            amount: 1,
            unit: ""
        };
    }

    const match = String(value).match(/^(\d+(?:\.\d+)?)(.*)$/);

    if (!match) {
        return {
            amount: 1,
            unit: ""
        };
    }

    return {
        amount: Number(match[1]),
        unit: match[2]
    };
}

const foodData = {
    favorite: {
        title: "お気に入り食材",
        items: (typeof favoriteItemsFromServer !== 'undefined' ? favoriteItemsFromServer : []).map(item => {

            const purchaseAmount = parsePurchaseAmount(item.purchaseAmount);

            return {
                id: item.id,
                name: item.name,
                categoryId: item.categoryId,
                category: item.categoryName,
                currentQuantityText: item.status === 0 ? "在庫切れ" : item.amount || "",
                status: item.status === 0 ? "out" : "normal",
                defaultStock: purchaseAmount.amount,
                unit: purchaseAmount.unit
            };
        })
    },
    food: {
        title: "購入履歴",
        items: []
    }
};


/* =========================
   Selection Modal
========================= */

/**
 * 食材一覧を API から取得する
 */
async function fetchFoodItems() {
    try {
        const response = await fetch("/shopping-list/api/foods");
        if (!response.ok) {
            throw new Error("食材一覧の取得に失敗しました");
        }
        const data = await response.json();

        // サーバ側で直近3回分の購入日ごとにグルーピングされたデータを想定
        // data = [ { purchaseDate: "2026-08-25", items: [ {id, name, purchaseAmount, categoryId}, ... ] }, ... ]
        foodData.food.groups = Array.isArray(data) ? data : [];

    } catch (error) {
        console.error("Error fetching food items:", error);
        alert("食材一覧の読み込みに失敗しました。");
    }
}

async function openSelectionModal(type) {
    currentSelectionType = type;

    // 食材一覧モーダルを開く時のみ API を呼び出す
    if (type === "food") {
        await fetchFoodItems();
    }

    renderSelectionList(type);
    document.getElementById("selectionModal")?.classList.add("show");
}

function renderSelectionList(type) {
    const data = foodData[type];
    if (!data) return;

    const title = document.getElementById("selectionTitle");
    if (title) title.textContent = data.title;

    const list = document.getElementById("selectionList");
    if (!list) return;

    list.innerHTML = "";

    if (type === "favorite") {
        const sortedItems = [...data.items].sort((a, b) => {
            const aOut = a.status === "out" ? 0 : 1;
            const bOut = b.status === "out" ? 0 : 1;
            if (aOut !== bOut) return aOut - bOut;
            const categoryA = categoryOrder.indexOf(a.category);
            const categoryB = categoryOrder.indexOf(b.category);
            return categoryA - categoryB;
        });

        let currentCategory = null;

        sortedItems.forEach(item => {
            if (currentCategory !== item.category) {
                currentCategory = item.category;
                const categoryHeader = document.createElement("div");
                categoryHeader.className = `shopping-selection-category ${getCategoryClass(item.category)}`;
                categoryHeader.textContent = item.category;
                list.appendChild(categoryHeader);
            }

            const row = document.createElement("div");
            row.className = "shopping-selection-item";

            let statusHtml = "";
            if (item.status === "out") {
                statusHtml = `<span class="shopping-stock-status shopping-stock-out">在庫切れ</span>`;
            }

            row.innerHTML = `
                <div class="shopping-selection-food">
                    <div class="shopping-selection-food-name">${escapeHtml(item.name)}</div>
                    <div class="shopping-selection-food-info">${statusHtml}</div>
                </div>
                <div class="shopping-selection-quantity">
                    <input type="number" class="shopping-selection-quantity-input" value="${escapeHtml(item.defaultStock)}" min="1" step="1">
                    <span class="shopping-selection-quantity-unit">${escapeHtml(item.unit || '')}</span>
                </div>
            `;

            row.dataset.id = item.id;
            row.dataset.categoryId = item.categoryId;
            row.dataset.category = item.category;
            row.dataset.name = item.name;
            row.dataset.unit = item.unit || '';

            list.appendChild(row);
            row.addEventListener("click", (e) => {
                if (e.target.tagName === 'INPUT') return;
                row.classList.toggle("selected");
            });
        });

    } else if (type === "food") {

        // data.groups: [ { purchaseDate: '2026-08-25', items: [...] }, ... ]
        const groups = data.groups || [];

        // iterate groups in order (assumed already sorted by server: newest first)
        groups.forEach(group => {
            const dateStr = group.purchaseDate; // e.g. '2026-08-25'
            const dateHeader = document.createElement("div");
            dateHeader.className = 'shopping-selection-category shopping-selection-date';
            dateHeader.textContent = formatDateJP(dateStr);
            list.appendChild(dateHeader);

            (group.items || []).forEach(item => {
                const parsed = parsePurchaseAmount(item.purchaseAmount);
                const defaultStock = parsed.amount || 1;
                const unit = parsed.unit || '';

                const row = document.createElement("div");
                row.className = "shopping-selection-item";

                row.innerHTML = `
                    <div class="shopping-selection-food">
                        <div class="shopping-selection-food-name">${escapeHtml(item.name)}</div>
                    </div>
                    <div class="shopping-selection-quantity">
                        <input type="number" class="shopping-selection-quantity-input" value="${escapeHtml(defaultStock)}" min="1" step="1">
                            <span class="shopping-selection-quantity-unit">${escapeHtml(unit || '')}</span>
                    </div>
                `;

                row.dataset.id = item.id;
                row.dataset.categoryId = item.categoryId;
                row.dataset.name = item.name;
                row.dataset.purchaseAmount = item.purchaseAmount;
                row.dataset.unit = unit || '';

                list.appendChild(row);
                row.addEventListener("click", (e) => {
                    if (e.target.tagName === 'INPUT') return;
                    row.classList.toggle("selected");
                });
            });
        });

    } else {
        // fallback: no items
    }
}

async function addSelectedFoods() {
    const selectedRows = document.querySelectorAll(".shopping-selection-item.selected");
    if (selectedRows.length === 0) {
        alert("追加する食材を選択してください。");
        return;
    }

    try {
        const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
        const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

        const headers = {
            "Content-Type": "application/json"
        };
        if (token && header) {
            headers[header] = token;
        }

        // 複数選択された食材を順番に追加する
        for (const row of Array.from(selectedRows)) {
            const name = row.dataset.name || row.querySelector(".shopping-selection-food-name")?.textContent.trim();
            const categoryId = row.dataset.categoryId;
            const id = row.dataset.id;
            const quantityInput = row.querySelector(".shopping-selection-quantity-input");
            const unit = row.querySelector(".shopping-selection-quantity-unit")?.textContent.trim();
            const quantity = Number(quantityInput?.value) || 1;
            const displayQuantity = `${quantity}${unit || ""}`;

            const body = JSON.stringify({
                itemId: Number(id),
                name: name,
                amount: displayQuantity,
                categoryId: Number(categoryId)
            });

            const response = await fetch("/shopping-list/add-new", {
                method: "POST",
                headers: headers,
                body: body
            });

            if (!response.ok) {
                throw new Error(`食材の追加に失敗しました: ${name}`);
            }

            const savedItem = await response.json();
            addShoppingItem(savedItem.id, name, displayQuantity, categoryId);
        }

        updateCount();
        closeModal("selectionModal");

    } catch (error) {
        console.error(error);
        alert("食材の登録に失敗しました。\n詳細はコンソールを確認してください。");
    }
}


/* =========================
   Quantity Modal
========================= */

function openQuantityModal(id, name, category, categoryId, defaultStock, unit) {
    selectedFood = { id, name, category, categoryId, defaultStock, unit };

    const title = document.getElementById("quantityTitle");
    if (title) title.textContent = `${name}の数量を入力`;

    const input = document.getElementById("quantityInput");
    if (input) input.value = defaultStock;

    const unitElement = document.getElementById("quantityUnit");
    if (unitElement) unitElement.textContent = unit;

    closeModal("selectionModal");
    document.getElementById("quantityModal")?.classList.add("show");

    setTimeout(() => {
        input?.focus();
        input?.select();
    }, 100);
}

async function confirmQuantity() {
    if (!selectedFood) return;

    const input = document.getElementById("quantityInput");
    const quantity = Number(input?.value);

    if (!Number.isInteger(quantity) || quantity <= 0) {
        alert("1以上の数量を入力してください。");
        input?.focus();
        return;
    }

    const displayQuantity = `${quantity}${selectedFood.unit || ""}`;

    try {
        const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
        const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

        const headers = {
            "Content-Type": "application/json"
        };
        if (token && header) {
            headers[header] = token;
        }

        const response = await fetch("/shopping-list/add-new", {
            method: "POST",
            headers: headers,
            body: JSON.stringify({
                itemId: Number(selectedFood.id),
                name: selectedFood.name,
                amount: displayQuantity,
                categoryId: Number(selectedFood.categoryId)
            })
        });

        if (!response.ok) {
            throw new Error("買い物リストへの保存に失敗しました。");
        }

        const savedItem = await response.json();

        addShoppingItem(savedItem.id, selectedFood.name, displayQuantity, selectedFood.categoryId);
        updateCount();

        closeModal("quantityModal");
        selectedFood = null;

    } catch (error) {
        console.error("Error saving item:", error);
        alert("食材の登録に失敗しました。");
    }
}

function cancelQuantityModal() {
    closeModal("quantityModal");
    selectedFood = null;
    if (currentSelectionType) {
        openSelectionModal(currentSelectionType);
    }
}

function formatDateJP(dateStr) {
    if (!dateStr) return "";
    const d = new Date(dateStr);
    if (isNaN(d.getTime())) return dateStr;
    const year = d.getFullYear();
    const month = d.getMonth() + 1;
    const day = d.getDate();
    return `${year}年${month}月${day}日`;
}


/* =========================
   Purchase
========================= */

function purchaseSelected() {
    const selected = document.querySelectorAll(".shopping-item.checked");

    if (selected.length === 0) {
        alert("購入済みにする食材を選択してください。");
        return;
    }

    const confirmList = document.getElementById("confirmList");
    if (!confirmList) return;

    confirmList.innerHTML = "";

    selected.forEach(item => {
        const name = item.querySelector(".shopping-item__food-name")?.textContent;
        const quantity = item.querySelector(".shopping-item__quantity")?.textContent;
        const row = document.createElement("div");

        row.className = "shopping-confirm-item";
        row.innerHTML = `
            <span>${escapeHtml(name)}（${escapeHtml(quantity)}）</span>
            <span class="shopping-confirm-date">購入日：${getTodayDate()}</span>
        `;
        confirmList.appendChild(row);
    });

    document.getElementById("purchaseModal")?.classList.add("show");
}

async function completePurchase() {
    const selected = document.querySelectorAll(".shopping-item.checked");
    if (selected.length === 0) return;

    try {
        for (const item of selected) {
            const shoppingListId = item.dataset.id;
            const itemId = item.dataset.itemId;
            const name = item.dataset.name;
            const amount = item.dataset.amount;
            const categoryId = item.dataset.categoryId;
            const favorite = item.dataset.favorite === "true";

            const data = {
                id: Number(shoppingListId),
                itemId: Number(itemId),
                name: name,
                amount: amount,
                categoryId: Number(categoryId),
                favorite: favorite,
                purchaseDate: getTodayDate()
            };

            const response = await fetch(`/shoppingList/${shoppingListId}/move-to-items`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error("食材一覧への登録に失敗しました。");
            }
        }

        closeModal("purchaseModal");
        alert("購入済みとして食材一覧へ追加しました。");
        window.location.reload();

    } catch (error) {
        console.error(error);
        alert("食材一覧への登録に失敗しました。");
    }
}


/* =========================
   Modal & Utility
========================= */

function closeModal(id) {
    document.getElementById(id)?.classList.remove("show");
}

function getTodayDate() {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}

function escapeHtml(value) {
    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}