
function handleRecipeSubmit() {
    const form = document.getElementById('recipeForm');
    const container = document.getElementById('selectedIdsContainer');
    const useSelected = document.getElementById('useSelected').checked; // 食材指定ボタンの状態

    container.innerHTML = '';

    // チェックされている食材を取得
    const checkedBoxes = document.querySelectorAll('.item-checkbox:checked');

    if (useSelected) {
        // 【食材を指定モード】
        if (checkedBoxes.length === 0) {
            alert("食材が指定されていません。");
            return; // 送信中止
        }

        // ★ ローディング画面を表示
        document.getElementById('loadingOverlay').style.display = 'flex';

        // IDをセット
        checkedBoxes.forEach(cb => {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'selectedIds';
            input.value = cb.value;
            container.appendChild(input);
        });
    } else {
        // 【おまかせモード】
        // selectedIdsを送らない、またはサーバー側の「全件/期限優先」ロジックに任せる
        console.log("おまかせモードで送信します");
    }

    form.submit();
}

// 「食材を指定」のON/OFFでテーブルの見た目を変える
document.getElementById('useSelected').addEventListener('change', function() {
    const table = document.querySelector('.table-container');
    if (this.checked) {
        table.classList.add('highlight-selection-mode');
        // もし1つも選んでなかったら、最初のチェックボックスを揺らすなどの演出もアリ
    } else {
        table.classList.remove('highlight-selection-mode');
    }
});

//戻るボタン対策: ブラウザの「戻る」で戻った時にローディングが残っていることがあるため
window.onpageshow = function(event) {
    if (event.persisted) {
        document.getElementById('loadingOverlay').style.display = 'none';
    }
};