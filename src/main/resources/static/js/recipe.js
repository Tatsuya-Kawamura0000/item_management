function handleRecipeSubmit() {
    const form = document.getElementById('recipeForm');
    const container = document.getElementById('selectedIdsContainer');
    const useSelectedElement = document.getElementById('useSelected');

    if (!form || !container || !useSelectedElement) {
        return;
    }

    const useSelected = useSelectedElement.checked;

    container.innerHTML = '';

    // チェックされている食材を取得
    const checkedBoxes = document.querySelectorAll('.item-checkbox:checked');

    if (useSelected) {
        // 【食材を指定モード】
        if (checkedBoxes.length === 0) {
            alert("食材が指定されていません。");
            return;
        }

        // ローディング画面を表示
        const loadingOverlay = document.getElementById('loadingOverlay');
        if (loadingOverlay) {
            loadingOverlay.style.display = 'flex';
        }

        // 選択した食材IDをhiddenに追加
        checkedBoxes.forEach(cb => {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'selectedIds';
            input.value = cb.value;
            container.appendChild(input);
        });
    } else {
        // 【おまかせモード】
        console.log("おまかせモードで送信します");
    }

    form.submit();
}


// =====================================================
// 「食材指定」のON/OFF
// =====================================================

const useSelectedElement = document.getElementById('useSelected');
const recipeModal = document.getElementById('recipeModal');

if (useSelectedElement && recipeModal) {
    useSelectedElement.addEventListener('change', function () {
        const table = document.querySelector('.table-container');

        if (this.checked) {
            // 食材選択モードON
            if (table) {
                table.classList.add('highlight-selection-mode');
            }

            // モーダルを開いたまま、背面の食材を操作できるようにする
            recipeModal.classList.add('allow-background-interaction');

        } else {
            // 食材選択モードOFF
            if (table) {
                table.classList.remove('highlight-selection-mode');
            }

            recipeModal.classList.remove('allow-background-interaction');
        }
    });
}


// =====================================================
// レシピ提案モーダル
// =====================================================

const openRecipeModalBtn =
    document.getElementById('openRecipeModalBtn');

const closeRecipeModalBtn =
    document.getElementById('closeRecipeModalBtn');


function openRecipeModal() {
    if (!recipeModal) {
        return;
    }

    recipeModal.classList.add('show');
    recipeModal.classList.remove('allow-background-interaction');
    recipeModal.setAttribute('aria-hidden', 'false');
}


function closeRecipeModal() {
    if (!recipeModal) {
        return;
    }

    // モーダルを閉じると食材指定モードも解除
    if (useSelectedElement) {
        useSelectedElement.checked = false;

        const table = document.querySelector('.table-container');
        if (table) {
            table.classList.remove('highlight-selection-mode');
        }
    }

    recipeModal.classList.remove('show');
    recipeModal.classList.remove('allow-background-interaction');
    recipeModal.setAttribute('aria-hidden', 'true');
}


// 「レシピ提案」ボタン
if (openRecipeModalBtn) {
    openRecipeModalBtn.addEventListener('click', openRecipeModal);
}


// 「×」ボタン
if (closeRecipeModalBtn) {
    closeRecipeModalBtn.addEventListener('click', closeRecipeModal);
}


// モーダルの背景クリックで閉じる
if (recipeModal) {
    recipeModal.addEventListener('click', function (event) {
        if (event.target === recipeModal) {
            closeRecipeModal();
        }
    });
}


// ESCキーで閉じる
document.addEventListener('keydown', function (event) {
    if (event.key === 'Escape') {
        closeRecipeModal();
    }
});


// =====================================================
// 戻るボタン対策
// =====================================================

window.onpageshow = function (event) {
    const loadingOverlay = document.getElementById('loadingOverlay');

    if (event.persisted && loadingOverlay) {
        loadingOverlay.style.display = 'none';
    }
};