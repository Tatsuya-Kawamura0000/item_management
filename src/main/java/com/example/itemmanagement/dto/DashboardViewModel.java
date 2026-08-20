package com.example.itemmanagement.dto;

import com.example.itemmanagement.entity.Items;
import java.util.List;

public class DashboardViewModel {

    private ItemSummary summary;
    private List<Items> soonFoods;
    private List<Items> expiredFoods;

    public DashboardViewModel(ItemSummary summary, List<Items> soonFoods, List<Items> expiredFoods) {
        this.summary = summary;
        this.soonFoods = soonFoods;
        this.expiredFoods = expiredFoods;
    }

    public ItemSummary getSummary() {
        return summary;
    }

    public List<Items> getSoonFoods() {
        return soonFoods;
    }

    public List<Items> getExpiredFoods() {
        return expiredFoods;
    }
}