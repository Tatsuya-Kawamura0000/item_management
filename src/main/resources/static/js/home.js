
// 選択されたIDを取得
function getSelectedItems() {

    const checkboxes = document.querySelectorAll(".item-checkbox:checked");

    const ids = [];

    checkboxes.forEach(cb => {
        ids.push(cb.value);
    });

    return ids;
}

//消費済みにする　ボタン押下時の処理
document.getElementById("bulkDeleteBtn").addEventListener("click", function () {

    const checkboxes = document.querySelectorAll(".item-checkbox:checked");

    if (checkboxes.length === 0) {
        showPopupAndReload("食材を選択してください", "error");
        return;
    }

    const ids = [];
    const favoriteItems = [];
    const favoriteIds = [];

    checkboxes.forEach(cb => {

        // value = item.id  削除対象アイテムのidを格納
        ids.push(cb.value);

        if (cb.dataset.favorite === "true") {
            favoriteItems.push(cb.dataset.name);
            favoriteIds.push(cb.value);
        }

    });

    // ① 消費確認
    if (!confirm("選択した食材を消費済みにしますか？")) {
        return;
    }

    const addIds = [];

    // ② お気に入りごとに確認
    for (let i = 0; i < favoriteItems.length; i++) {

        const itemName = favoriteItems[i];
        const itemId = favoriteIds[i];

        const message =
            itemName + " はお気に入りの食材です。\n買い物リストに追加しますか？";

        if (confirm(message)) {
            addIds.push(itemId);  //買い物リストへ追加するアイテムのidを格納していく
        }

    }


    if (addIds.length > 0) {

        // 買い物リストに追加
        fetch("/users/bulk-add-shopping-list", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(addIds)
        })
            .then(async response => {

                if (response.ok) {

                    deleteItems(ids);

                } else {

                    const message = await response.text();

                    if (message.includes("すでに")) {
                        showPopupAndReload(message, "error");    //重複あった場合のメッセージ
                        deleteItems(ids);　//アイテムは削除
                    } else {
                        showPopupAndReload("買い物リスト追加に失敗しました", "error");
                    }

                }

            })
            .catch(error => {
                console.error(error);
                showPopupAndReload("通信エラーが発生しました", "error");
            });
    } else {

        deleteItems(ids);
    }
});

function deleteItems(ids) {

    fetch("/users/bulk-delete", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(ids)
    })
        .then(response => {

            if (response.ok) {
                showPopupAndReload("消費済みにしました");
            } else {
                showPopupAndReload("削除に失敗しました", "error");
            }

        });

}

// 全選択チェックボックス
document.getElementById("selectAll").addEventListener("change", function() {

    const checked = this.checked;

    const checkboxes = document.querySelectorAll(".item-checkbox");

    checkboxes.forEach(cb => {
        cb.checked = checked;
    });

});


//買い物リストへ追加する　ボタン押下時の処理

document.getElementById("bulkShoppingBtn").addEventListener("click", function() {

    const ids = getSelectedItems();

    if (ids.length === 0) {
        showPopupAndReload("食材を選択してください", "error");
        return;
    }

    if (!confirm("選択した食材を買い物リストに追加しますか？")) {
        return;
    }

    fetch("/users/bulk-add-shopping-list", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(ids)
    })
        .then(async response => {

            if (response.ok) {

                showPopupAndReload("買い物リストに追加しました！");

            } else {
                //二重登録になる場合
                const message = await response.text();
                showPopupAndReload(message, "error");  //　～はすでに買い物リストに存在しています

            }

        })
        .catch(error => {
            console.error(error);
            showPopupAndReload("通信エラーが発生しました", "error");
        });

});

