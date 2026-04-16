package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.HomeViewModel;
import com.example.itemmanagement.dto.ItemSummary;
import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.entity.Items;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HomeService {

    private final GetAllItemsService itemService;
    private final ItemDeadlineService deadlineService;
    private final ItemSummaryService summaryService;

    // ★ コンストラクタインジェクション
    public HomeService(
            GetAllItemsService itemService,
            ItemDeadlineService deadlineService,
            ItemSummaryService summaryService) {

        this.itemService = itemService;
        this.deadlineService = deadlineService;
        this.summaryService = summaryService;
    }

    public HomeViewModel getHomeData(Integer userId) {

        // ユーザーごとの食材一覧取得
        List<Items> items = itemService.getAllItems(userId);

        // 今はフィルタなしなのでそのまま
        List<Items> filteredItems = items;

        deadlineService.applyDeadlineMessage(items);

        ItemSummary summary = summaryService.summarize(items);

        // ※ 仮で空（後で追加する）
        List<Categories> categories = new ArrayList<>();
        int shoppingCount = 0;                                    //買い物リストのアイテム数を取得int shoppingCount = addToShoppingListService.getShoppingListCount(userId);
        Map<Integer, Integer> categoryCounts = new HashMap<>();

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
