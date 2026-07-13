package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.HomeViewModel;
import com.example.itemmanagement.dto.ItemSummary;
import com.example.itemmanagement.dto.SearchViewModel;
import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.entity.Items;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HomeService {

    private final ItemQueryService itemQueryService;
    private final ItemDeadlineService deadlineService;
    private final ItemSummaryService summaryService;
    private final CategoryService categoryService;
    private final AddToShoppingListService addToShoppingListService;


    // ★ コンストラクタインジェクション
    public HomeService(ItemQueryService itemQueryService, ItemDeadlineService deadlineService,
                       ItemSummaryService summaryService,
                       CategoryService categoryService,
                       AddToShoppingListService addToShoppingListService) {

        this.itemQueryService = itemQueryService;
        this.deadlineService = deadlineService;
        this.summaryService = summaryService;
        this.categoryService = categoryService;
        this.addToShoppingListService = addToShoppingListService;

    }

    public HomeViewModel getHomeData(Integer userId, Integer category, Boolean expiringSoon, Boolean expired) {

        List<Items> items = itemQueryService.getAllItems(userId);

        List<Items> filteredItems;

        if (category != null || expiringSoon != null || expired != null) {
            filteredItems = itemQueryService.filterItems(    //フィルター適用アイテム適用
                    category, expiringSoon, expired, userId);
        } else {
            filteredItems = items;   //全件格納
        }

        deadlineService.applyDeadlineMessage(filteredItems);  //期限メッセージをセット

        ItemSummary summary = summaryService.summarize(items);  //期限切れ、期限間近アイテム件数集計

        List<Categories> categories = categoryService.getAllCategories();  //カテゴリ情報取得

        int shoppingCount = addToShoppingListService.getShoppingListCount(userId);  //買い物リストのアイテム件数カウント

        Map<Integer, Integer> categoryCounts = categoryService.getCategoryCounts(userId);  //カテゴリ毎のアイテム件数カウント

        return new HomeViewModel(items, filteredItems, categories, summary.getExpiredCount(), summary.getWarningCount(), shoppingCount, categoryCounts);
    }


    //検索かけられたとき
    public SearchViewModel getSearchData(Integer userId, String searchType, String keyword) {


        List<Items> items = itemQueryService.getAllItems(userId);

        //検索結果を格納
        List<Items> result = itemQueryService.search(searchType, keyword, userId);


        deadlineService.applyDeadlineMessage(result);  //期限メッセージをセット

        ItemSummary summary = summaryService.summarize(items);  //期限切れ、期限間近アイテム件数集計

        List<Categories> categories = categoryService.getAllCategories();  //カテゴリ情報取得

        int shoppingCount = addToShoppingListService.getShoppingListCount(userId);  //買い物リストのアイテム件数カウント

        Map<Integer, Integer> categoryCounts = categoryService.getCategoryCounts(userId);  //カテゴリ毎のアイテム件数カウント


        return new SearchViewModel(items, result,  //検索結果
                categories, summary.getExpiredCount(), summary.getWarningCount(), shoppingCount, categoryCounts);
    }

}
