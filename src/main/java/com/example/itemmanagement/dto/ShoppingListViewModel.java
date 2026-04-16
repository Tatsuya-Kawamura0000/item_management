package com.example.itemmanagement.dto;

import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.entity.ShoppingListItem;

import java.util.List;

public class ShoppingListViewModel {

    private final List<ShoppingListItem> listItems;
    private final List<Categories> categories;

    public ShoppingListViewModel(
            List<ShoppingListItem> listItems,
            List<Categories> categories) {

        this.listItems = listItems;
        this.categories = categories;

    }

    public List<ShoppingListItem> getListItems() {
        return listItems;
    }

    public List<Categories> getCategories() {
        return categories;
    }
}
