package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.HomeViewModel;
import com.example.itemmanagement.dto.ItemSummary;
import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.entity.Items;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HomeService {

    private final GetAllItemsService itemService;
    private final ItemDeadlineService deadlineService;
    private final ItemSummaryService summaryService;
    private final GetFilterItemsService getFilterItemsService;
    private final GetAllCategoriesService getAllCategoriesService;
    private final AddToShoppingListService addToShoppingListService;

    // ★ コンストラクタインジェクション
    public HomeService(
            GetAllItemsService itemService,
            ItemDeadlineService deadlineService,
            ItemSummaryService summaryService,
            GetFilterItemsService getFilterItemsService,
            GetAllCategoriesService getAllCategoriesService,
            AddToShoppingListService addToShoppingListService) {

        this.itemService = itemService;
        this.deadlineService = deadlineService;
        this.summaryService = summaryService;
        this.getFilterItemsService = getFilterItemsService;
        this.getAllCategoriesService = getAllCategoriesService;
        this.addToShoppingListService = addToShoppingListService;
    }

    public HomeViewModel getHomeData(
            Integer userId,
            Integer category,
            Boolean expiringSoon,
            Boolean expired) {

        List<Items> items = itemService.getAllItems(userId);

        List<Items> filteredItems;

        if (category != null || expiringSoon != null || expired != null) {
            filteredItems = getFilterItemsService.filterItems(    //フィルター適用アイテム適用
                    category, expiringSoon, expired, userId);
        } else {
            filteredItems = items;   //全件格納
        }

        deadlineService.applyDeadlineMessage(filteredItems);  //期限メッセージをセット

        ItemSummary summary = summaryService.summarize(items);  //期限切れ、期限間近アイテム件数集計

        List<Categories> categories = getAllCategoriesService.getAllCategories();  //カテゴリ情報取得

        int shoppingCount = addToShoppingListService.getShoppingListCount(userId);  //買い物リストのアイテム件数カウント

        Map<Integer, Integer> categoryCounts =
                getAllCategoriesService.getCategoryCounts(userId);  //カテゴリ毎のアイテム件数カウント

        return new HomeViewModel(
                items,
                filteredItems,
                categories,
                summary.getExpiredCount(),
                summary.getWarningCount(),
                shoppingCount,
                categoryCounts
        );
    }
}
