/* ========================================
   要素の取得
======================================== */
const foodCards = document.querySelectorAll('.food-card');
const consumeButton = document.getElementById('consumeButton');
const consumeCount = document.getElementById('consumeCount');

// モーダル要素
const consumeModal = document.getElementById('consumeModal');
const modalMessage = document.getElementById('modalMessage');
const modalCancelBtn = document.getElementById('modalCancelBtn');
const modalConfirmBtn = document.getElementById('modalConfirmBtn');

// 選択状態を保持するSet
const selectedCards = new Set();


/* ========================================
   カードの選択・解除処理
======================================== */
foodCards.forEach(card => {
    card.addEventListener('click', () => {
        if (selectedCards.has(card)) {
            selectedCards.delete(card);
            card.classList.remove('selected');
        } else {
            selectedCards.add(card);
            card.classList.add('selected');
        }
        updateConsumeButton();
    });
});


/* ========================================
   「消費済みにする」ボタン表示更新
======================================== */
function updateConsumeButton() {
    const count = selectedCards.size;

    if (count > 0) {
        consumeButton.classList.add('show');
        consumeCount.textContent = count + '件';
    } else {
        consumeButton.classList.remove('show');
    }
}


/* ========================================
   ポップアップモーダル表示・非表示
======================================== */
// 「消費済みにする」ボタンクリック時にモーダルを開く
consumeButton.addEventListener('click', () => {
    const count = selectedCards.size;
    if (count === 0) return;

    modalMessage.textContent = `選択した ${count} 件の食材を消費済みにしますか？`;
    consumeModal.classList.add('show');
});

// キャンセルボタンでモーダルを閉じる
modalCancelBtn.addEventListener('click', closeModal);

// 背景領域タップでモーダルを閉じる
consumeModal.addEventListener('click', (e) => {
    if (e.target === consumeModal) {
        closeModal();
    }
});

function closeModal() {
    consumeModal.classList.remove('show');
}


/* ========================================
   消費確定時の処理
======================================== */
modalConfirmBtn.addEventListener('click', () => {
    // 選択されたカードをDOMから削除（バックエンド連携時はここでAPI呼出などを実行）
    selectedCards.forEach(card => {
        card.remove();
    });

    // 状態のクリア
    selectedCards.clear();
    updateConsumeButton();
    closeModal();
});


/* ========================================
   ログアウト処理（仮）
======================================== */
function logout() {
    alert('ログアウトします');
}