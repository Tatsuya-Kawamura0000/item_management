package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.DashboardViewModel;
import com.example.itemmanagement.dto.ItemSummary;
import com.example.itemmanagement.entity.Items;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemSummaryService {

    @Autowired
    private ItemQueryService itemQueryService;

    @Autowired
    private ItemDeadlineService itemDeadlineService;

    public ItemSummary summarize(List<Items> items) {

        int expired = 0;
        int warning = 0;

        for (Items item : items) {
            if (item.getDeadline() != null) {

                long days = ChronoUnit.DAYS.between(LocalDate.now(), item.getDeadline());

                if (days < 0) expired++;         // 期限切れアイテムをカウント
                else if (days <= 3) warning++;   // 期限間近アイテムをカウント
            }
        }

        return new ItemSummary(expired, warning, items.size());
    }

    public DashboardViewModel getDashboardData(Integer userId) {
        // 1. 全食材取得
        List<Items> items = itemQueryService.getAllItems(userId);

        // 2. メッセージおよびフラグの設定
        itemDeadlineService.applyDeadlineMessage(items);

        // 3. サマリーの計算（自クラスのメソッドを直接呼ぶ）
        ItemSummary summary = summarize(items);

        // 4. SOON / EXPIRED の抽出ロジック
        List<Items> soonFoods = items.stream()
                .filter(Items::isExpiringSoon)
                .collect(Collectors.toList());

        List<Items> expiredFoods = items.stream()
                .filter(Items::isExpired)
                .collect(Collectors.toList());

        return new DashboardViewModel(summary, soonFoods, expiredFoods);
    }
}