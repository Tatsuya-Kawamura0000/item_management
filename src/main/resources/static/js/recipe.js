// =====================================================
// レシピ画面 JS (カテゴリ切替・人前自動計算・CRUDモーダル連動)
// =====================================================

document.addEventListener('DOMContentLoaded', function () {
    // --------------------------------------------------
    // モーダル要素の初期化
    // --------------------------------------------------
    const registerModal = document.getElementById('recipeRegisterModal');
    const editModal = document.getElementById('recipeEditModal');
    const deleteModal = document.getElementById('recipeDeleteModal');

    const openRegisterBtn = document.getElementById('openRegisterModalBtn');
    const closeRegisterBtn = document.getElementById('closeRegisterModalBtn');
    const cancelRegisterBtn = document.getElementById('btnCancelRegister');

    const closeEditBtn = document.getElementById('closeEditModalBtn');
    const cancelEditBtn = document.getElementById('btnCancelEdit');

    const closeDeleteBtn = document.getElementById('closeDeleteModalBtn');
    const cancelDeleteBtn = document.getElementById('btnCancelDelete');

    // 登録モーダル
    if (openRegisterBtn) openRegisterBtn.addEventListener('click', () => openModal(registerModal));
    if (closeRegisterBtn) closeRegisterBtn.addEventListener('click', () => closeModal(registerModal));
    if (cancelRegisterBtn) cancelRegisterBtn.addEventListener('click', () => closeModal(registerModal));

    // 編集モーダル
    if (closeEditBtn) closeEditBtn.addEventListener('click', () => closeModal(editModal));
    if (cancelEditBtn) cancelEditBtn.addEventListener('click', () => closeModal(editModal));

    // 削除モーダル
    if (closeDeleteBtn) closeDeleteBtn.addEventListener('click', () => closeModal(deleteModal));
    if (cancelDeleteBtn) cancelDeleteBtn.addEventListener('click', () => closeModal(deleteModal));

    // 背景クリックで閉じる
    [registerModal, editModal, deleteModal].forEach(modal => {
        if (modal) {
            modal.addEventListener('click', function (e) {
                if (e.target === modal) closeModal(modal);
            });
        }
    });

    // Escキーで閉じる
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            closeModal(registerModal);
            closeModal(editModal);
            closeModal(deleteModal);
        }
    });

    // 登録モーダル: 行追加ボタン
    const btnAddIngredient = document.getElementById('btnAddIngredient');
    if (btnAddIngredient) btnAddIngredient.addEventListener('click', () => addIngredientRow('ingredientListContainer', 'ingredients'));

    const btnAddStep = document.getElementById('btnAddStep');
    if (btnAddStep) btnAddStep.addEventListener('click', () => addStepRow('stepListContainer', 'steps'));

    // 編集モーダル: 行追加ボタン
    const btnEditAddIngredient = document.getElementById('btnEditAddIngredient');
    if (btnEditAddIngredient) btnEditAddIngredient.addEventListener('click', () => addIngredientRow('editIngredientListContainer', 'ingredients'));

    const btnEditAddStep = document.getElementById('btnEditAddStep');
    if (btnEditAddStep) btnEditAddStep.addEventListener('click', () => addStepRow('editStepListContainer', 'steps'));

    // トーストメッセージの自動フェードアウト
    setupToast('toastMessage');
    setupToast('toastErrorMessage');

    // --------------------------------------------------
    // カテゴリカード選択 & 検索フィルタリング
    // --------------------------------------------------
    initCategoryFilter();
    initSearchFilter();
});

// --------------------------------------------------
// モーダル共通制御
// --------------------------------------------------
function openModal(modal) {
    if (modal) {
        modal.classList.add('show');
        modal.setAttribute('aria-hidden', 'false');
        document.body.style.overflow = 'hidden';
    }
}

function closeModal(modal) {
    if (modal) {
        modal.classList.remove('show');
        modal.setAttribute('aria-hidden', 'true');
        document.body.style.overflow = '';
    }
}

function setupToast(toastId) {
    const toast = document.getElementById(toastId);
    if (toast) {
        setTimeout(function () {
            toast.style.transition = 'opacity 0.5s ease';
            toast.style.opacity = '0';
            setTimeout(function () {
                toast.remove();
            }, 500);
        }, 3500);
    }
}

// --------------------------------------------------
// カテゴリ切替 & 検索絞り込みロジック
// --------------------------------------------------
let activeCategory = 'all';

function initCategoryFilter() {
    const categoryCards = document.querySelectorAll('.category-select-card');
    categoryCards.forEach(card => {
        card.addEventListener('click', function () {
            categoryCards.forEach(c => c.classList.remove('active'));
            this.classList.add('active');
            activeCategory = this.getAttribute('data-category');
            applyFilters();
        });
    });
}

function initSearchFilter() {
    const searchInput = document.getElementById('recipeSearchInput');
    const searchClear = document.getElementById('recipeSearchClear');

    if (!searchInput) return;

    searchInput.addEventListener('input', function () {
        if (searchClear) {
            searchClear.style.display = this.value.trim() ? 'block' : 'none';
        }
        applyFilters();
    });

    if (searchClear) {
        searchClear.addEventListener('click', function () {
            searchInput.value = '';
            searchClear.style.display = 'none';
            searchInput.focus();
            applyFilters();
        });
    }
}

function applyFilters() {
    const searchInput = document.getElementById('recipeSearchInput');
    const keyword = searchInput ? searchInput.value.trim().toLowerCase() : '';
    const recipeBoxes = document.querySelectorAll('.recipe-card-box');
    const emptyState = document.getElementById('recipeEmptyState');
    const countDisplay = document.getElementById('filteredRecipeCount');

    let visibleCount = 0;

    recipeBoxes.forEach(box => {
        const catId = box.getAttribute('data-category-id');
        const titleElem = box.querySelector('.recipe-title-text');
        const foodsElem = box.querySelector('.recipe-foods');

        const titleText = titleElem ? titleElem.textContent.toLowerCase() : '';
        const foodsText = foodsElem ? foodsElem.textContent.toLowerCase() : '';

        const matchesCategory = (activeCategory === 'all') || (catId === activeCategory);
        const matchesKeyword = !keyword || titleText.includes(keyword) || foodsText.includes(keyword);

        if (matchesCategory && matchesKeyword) {
            box.style.display = 'block';
            visibleCount++;
        } else {
            box.style.display = 'none';
        }
    });

    if (countDisplay) countDisplay.textContent = visibleCount;
    if (emptyState) emptyState.style.display = (visibleCount === 0) ? 'flex' : 'none';
}

// --------------------------------------------------
// レシピ詳細アコーディオン開閉
// --------------------------------------------------
function toggleRecipeDetails(triggerElement) {
    const cardBox = triggerElement.closest('.recipe-card-box');
    if (!cardBox) return;

    cardBox.classList.toggle('is-open');
}

// --------------------------------------------------
// ① 人前変更 & 材料分量の動的自動計算
// --------------------------------------------------
function changeServings(btn, delta) {
    const cardBox = btn.closest('.recipe-card-box');
    if (!cardBox) return;

    const baseServings = parseFloat(cardBox.getAttribute('data-base-servings')) || 2;
    const servingsDisplay = cardBox.querySelector('.servings-num-display');
    if (!servingsDisplay) return;

    let currentServings = parseInt(servingsDisplay.textContent, 10) || baseServings;
    let newServings = currentServings + delta;

    if (newServings < 1) newServings = 1;
    if (newServings > 20) newServings = 20;

    servingsDisplay.textContent = newServings;

    // 各材料の分量を再計算
    const ratio = newServings / baseServings;
    const calcQtyElements = cardBox.querySelectorAll('.calc-qty');

    calcQtyElements.forEach(elem => {
        const baseQtyStr = elem.getAttribute('data-base-qty');
        if (!baseQtyStr || isNaN(baseQtyStr) || baseQtyStr.trim() === '') return;

        const baseQty = parseFloat(baseQtyStr);
        if (isNaN(baseQty)) return;

        const calculatedQty = baseQty * ratio;
        elem.textContent = formatQuantity(calculatedQty);
    });
}

function formatQuantity(num) {
    if (num == null || isNaN(num)) return '';
    // 整数ならそのまま、小数なら端数を綺麗に表示
    if (Number.isInteger(num)) {
        return num.toString();
    }
    // 小数点第2位まで、末尾の0はカット
    const fixed = num.toFixed(2);
    return parseFloat(fixed).toString();
}

// --------------------------------------------------
// ② レシピ編集機能 (API取得 & モーダル投入)
// --------------------------------------------------
function openEditModal(recipeId) {
    const editModal = document.getElementById('recipeEditModal');
    const editForm = document.getElementById('recipeEditForm');
    if (!editModal || !editForm) return;

    editForm.action = `/recipes/edit/${recipeId}`;

    // APIから最新データをフェッチ
    fetch(`/recipes/api/${recipeId}`)
        .then(response => {
            if (!response.ok) throw new Error('レシピ情報の取得に失敗しました');
            return response.json();
        })
        .then(recipe => {
            // 基本情報のセット
            document.getElementById('editRecipeId').value = recipe.id || '';
            document.getElementById('editRecipeName').value = recipe.recipeName || '';
            document.getElementById('editServings').value = recipe.servings || 2;
            document.getElementById('editCategoryId').value = recipe.categoryId || '';
            document.getElementById('editMemo').value = recipe.memo || '';
            document.getElementById('editSourceUrl').value = recipe.sourceUrl || '';

            // 材料のレンダリング
            const ingContainer = document.getElementById('editIngredientListContainer');
            ingContainer.innerHTML = '';
            if (recipe.ingredients && recipe.ingredients.length > 0) {
                recipe.ingredients.forEach((ing, index) => {
                    renderEditIngredientRow(ingContainer, ing, index);
                });
            } else {
                addIngredientRow('editIngredientListContainer', 'ingredients');
            }

            // 手順のレンダリング
            const stepContainer = document.getElementById('editStepListContainer');
            stepContainer.innerHTML = '';
            if (recipe.steps && recipe.steps.length > 0) {
                recipe.steps.forEach((step, index) => {
                    renderEditStepRow(stepContainer, step, index);
                });
            } else {
                addStepRow('editStepListContainer', 'steps');
            }

            openModal(editModal);
        })
        .catch(err => {
            console.error(err);
            alert('レシピデータの読み込みに失敗しました。');
        });
}

function renderEditIngredientRow(container, ing, index) {
    let categoryOptionsHtml = '<option value="">食材カテゴリ</option>';
    if (typeof ingredientCategoryOptions !== 'undefined' && Array.isArray(ingredientCategoryOptions)) {
        ingredientCategoryOptions.forEach(cat => {
            const selected = (ing.categoryId && String(ing.categoryId) === String(cat.id)) ? 'selected' : '';
            categoryOptionsHtml += `<option value="${cat.id}" ${selected}>${cat.name}</option>`;
        });
    }

    const rowDiv = document.createElement('div');
    rowDiv.className = 'ingredient-row';
    rowDiv.setAttribute('data-index', index);
    rowDiv.innerHTML = `
        <div class="ing-col name-col">
            <input type="text" name="ingredients[${index}].ingredientName" value="${escapeHtml(ing.ingredientName || '')}" class="form-input ing-name-input" placeholder="材料名 (例: 牛肉)">
        </div>
        <div class="ing-col qty-col">
            <input type="number" step="0.01" name="ingredients[${index}].quantity" value="${ing.quantity != null ? ing.quantity : ''}" class="form-input ing-qty-input" placeholder="分量">
        </div>
        <div class="ing-col unit-col">
            <input type="text" name="ingredients[${index}].unit" value="${escapeHtml(ing.unit || '')}" class="form-input ing-unit-input" placeholder="単位 (g, 個, 大さじ)">
        </div>
        <div class="ing-col cat-col">
            <select name="ingredients[${index}].categoryId" class="form-select ing-cat-select">
                ${categoryOptionsHtml}
            </select>
        </div>
        <div class="ing-col main-col">
            <label class="main-checkbox-label" title="主要材料">
                <input type="checkbox" name="ingredients[${index}].isMain" value="true" ${ing.isMain ? 'checked' : ''}>
                <span><i class="fa-solid fa-star"></i> 主要</span>
            </label>
        </div>
        <button type="button" class="btn-remove-row" onclick="removeIngredientRow(this)" title="削除">
            <i class="fa-solid fa-xmark"></i>
        </button>
    `;
    container.appendChild(rowDiv);
}

function renderEditStepRow(container, step, index) {
    const stepNumber = index + 1;
    const rowDiv = document.createElement('div');
    rowDiv.className = 'step-row';
    rowDiv.setAttribute('data-index', index);
    rowDiv.innerHTML = `
        <span class="step-number">${stepNumber}</span>
        <div class="step-content-col">
            <textarea name="steps[${index}].instruction" class="form-textarea step-textarea" placeholder="調理手順を入力してください" rows="2">${escapeHtml(step.instruction || '')}</textarea>
        </div>
        <button type="button" class="btn-remove-row" onclick="removeStepRow(this)" title="削除">
            <i class="fa-solid fa-xmark"></i>
        </button>
    `;
    container.appendChild(rowDiv);
}

// --------------------------------------------------
// ② レシピ削除機能 (確認ダイアログ & 送信)
// --------------------------------------------------
function openDeleteModal(recipeId, recipeName) {
    const deleteModal = document.getElementById('recipeDeleteModal');
    const deleteForm = document.getElementById('recipeDeleteForm');
    const nameSpan = document.getElementById('deleteTargetRecipeName');

    if (!deleteModal || !deleteForm) return;

    deleteForm.action = `/recipes/delete/${recipeId}`;
    if (nameSpan) nameSpan.textContent = recipeName || 'このレシピ';

    openModal(deleteModal);
}

// --------------------------------------------------
// 行追加・削除 共通ユーティリティ
// --------------------------------------------------
function addIngredientRow(containerId, fieldPrefix = 'ingredients') {
    const container = document.getElementById(containerId);
    if (!container) return;

    const currentRows = container.querySelectorAll('.ingredient-row');
    const newIndex = currentRows.length;

    let categoryOptionsHtml = '<option value="">食材カテゴリ</option>';
    if (typeof ingredientCategoryOptions !== 'undefined' && Array.isArray(ingredientCategoryOptions)) {
        ingredientCategoryOptions.forEach(cat => {
            categoryOptionsHtml += `<option value="${cat.id}">${cat.name}</option>`;
        });
    }

    const rowDiv = document.createElement('div');
    rowDiv.className = 'ingredient-row';
    rowDiv.setAttribute('data-index', newIndex);
    rowDiv.innerHTML = `
        <div class="ing-col name-col">
            <input type="text" name="${fieldPrefix}[${newIndex}].ingredientName" class="form-input ing-name-input" placeholder="材料名 (例: 牛肉)">
        </div>
        <div class="ing-col qty-col">
            <input type="number" step="0.01" name="${fieldPrefix}[${newIndex}].quantity" class="form-input ing-qty-input" placeholder="分量">
        </div>
        <div class="ing-col unit-col">
            <input type="text" name="${fieldPrefix}[${newIndex}].unit" class="form-input ing-unit-input" placeholder="単位 (g, 個, 大さじ)">
        </div>
        <div class="ing-col cat-col">
            <select name="${fieldPrefix}[${newIndex}].categoryId" class="form-select ing-cat-select">
                ${categoryOptionsHtml}
            </select>
        </div>
        <div class="ing-col main-col">
            <label class="main-checkbox-label" title="主要材料">
                <input type="checkbox" name="${fieldPrefix}[${newIndex}].isMain" value="true">
                <span><i class="fa-solid fa-star"></i> 主要</span>
            </label>
        </div>
        <button type="button" class="btn-remove-row" onclick="removeIngredientRow(this)" title="削除">
            <i class="fa-solid fa-xmark"></i>
        </button>
    `;

    container.appendChild(rowDiv);
}

function removeIngredientRow(btn) {
    const row = btn.closest('.ingredient-row');
    const container = row ? row.parentElement : null;
    if (row && container) {
        const rows = container.querySelectorAll('.ingredient-row');
        if (rows.length <= 1) {
            row.querySelectorAll('input').forEach(input => {
                if (input.type === 'checkbox') input.checked = false;
                else input.value = '';
            });
            const select = row.querySelector('select');
            if (select) select.value = '';
            return;
        }

        row.remove();
        reindexIngredients(container);
    }
}

function reindexIngredients(container) {
    if (!container) return;
    const rows = container.querySelectorAll('.ingredient-row');
    rows.forEach((row, index) => {
        row.setAttribute('data-index', index);
        const nameInput = row.querySelector('.ing-name-input');
        const qtyInput = row.querySelector('.ing-qty-input');
        const unitInput = row.querySelector('.ing-unit-input');
        const catSelect = row.querySelector('.ing-cat-select');
        const mainCheckbox = row.querySelector('input[type="checkbox"]');

        if (nameInput) nameInput.name = `ingredients[${index}].ingredientName`;
        if (qtyInput) qtyInput.name = `ingredients[${index}].quantity`;
        if (unitInput) unitInput.name = `ingredients[${index}].unit`;
        if (catSelect) catSelect.name = `ingredients[${index}].categoryId`;
        if (mainCheckbox) mainCheckbox.name = `ingredients[${index}].isMain`;
    });
}

function addStepRow(containerId, fieldPrefix = 'steps') {
    const container = document.getElementById(containerId);
    if (!container) return;

    const currentRows = container.querySelectorAll('.step-row');
    const newIndex = currentRows.length;
    const stepNumber = newIndex + 1;

    const rowDiv = document.createElement('div');
    rowDiv.className = 'step-row';
    rowDiv.setAttribute('data-index', newIndex);
    rowDiv.innerHTML = `
        <span class="step-number">${stepNumber}</span>
        <div class="step-content-col">
            <textarea name="${fieldPrefix}[${newIndex}].instruction" class="form-textarea step-textarea" placeholder="調理手順を入力してください" rows="2"></textarea>
        </div>
        <button type="button" class="btn-remove-row" onclick="removeStepRow(this)" title="削除">
            <i class="fa-solid fa-xmark"></i>
        </button>
    `;

    container.appendChild(rowDiv);
}

function removeStepRow(btn) {
    const row = btn.closest('.step-row');
    const container = row ? row.parentElement : null;
    if (row && container) {
        const rows = container.querySelectorAll('.step-row');
        if (rows.length <= 1) {
            const textarea = row.querySelector('textarea');
            if (textarea) textarea.value = '';
            return;
        }

        row.remove();
        reindexSteps(container);
    }
}

function reindexSteps(container) {
    if (!container) return;
    const rows = container.querySelectorAll('.step-row');
    rows.forEach((row, index) => {
        row.setAttribute('data-index', index);
        const numberSpan = row.querySelector('.step-number');
        if (numberSpan) numberSpan.textContent = index + 1;
        const textarea = row.querySelector('.step-textarea');
        if (textarea) textarea.name = `steps[${index}].instruction`;
    });
}

function escapeHtml(str) {
    if (!str) return '';
    return str.toString()
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}