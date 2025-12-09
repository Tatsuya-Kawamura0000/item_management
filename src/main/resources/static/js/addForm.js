$(function () {
    const heartBtn = $('#favoriteButton i'); // iタグを対象
    const field = $('#favoriteField');

    // 初期表示
    const isFavorite = field.val() === "true";
    heartBtn.css('color', isFavorite ? '#ffb6c1' : 'gray');

    // クリックで色切替
    $('#favoriteButton').click(function() {
        let fav = field.val() === "true";
        fav = !fav;
        field.val(fav.toString());
        heartBtn.css('color', fav ? '#ffb6c1' : 'gray');
    });

});