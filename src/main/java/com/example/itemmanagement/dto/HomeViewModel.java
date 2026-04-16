package com.example.itemmanagement.dto;

import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.entity.Items;

import java.util.List;
import java.util.Map;


public class HomeViewModel {

    private List<Items> items;
    private List<Items> filteredItems;
    private List<Categories> categories;
    private int expiredCount;
    private int warningCount;
    private int shoppingCount;
    private Map<Integer, Integer> categoryCounts;

    public HomeViewModel(
            List<Items> items,
            List<Items> filteredItems,
            List<Categories> categories,
            int expiredCount,
            int warningCount,
            int shoppingCount,
            Map<Integer, Integer> categoryCounts) {

        this.items = items;
        this.filteredItems = filteredItems;
        this.categories = categories;
        this.expiredCount = expiredCount;
        this.warningCount = warningCount;
        this.shoppingCount = shoppingCount;
        this.categoryCounts = categoryCounts;
    }

    // getter
    public List<Items> getItems() { return items; }
    public List<Items> getFilteredItems() { return filteredItems; }
    public List<Categories> getCategories() { return categories; }
    public int getExpiredCount() { return expiredCount; }
    public int getWarningCount() { return warningCount; }
    public int getShoppingCount() { return shoppingCount; }
    public Map<Integer, Integer> getCategoryCounts() { return categoryCounts; }
}
