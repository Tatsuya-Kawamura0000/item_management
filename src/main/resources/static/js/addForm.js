$(function () {
    const heartBtn = $('#favoriteButton i'); // iタグを対象
    const field = $('#favoriteField');

    // 初期表示
    const isFavorite = field.val() === "true";
    heartBtn.css('color', isFavorite ? '#d81b60' : 'gray');

    // クリックで色切替
    $('#favoriteButton').click(function() {
        let fav = field.val() === "true";
        fav = !fav;
        field.val(fav.toString());
        heartBtn.css('color', fav ? '#d81b60' : 'gray');
    });

    // 成功メッセージ3秒で消す
    const message = document.getElementById("successMessage");

    if (message) {
        setTimeout(function () {
            $('#successMessage').fadeOut();
        }, 3000);
    }
});