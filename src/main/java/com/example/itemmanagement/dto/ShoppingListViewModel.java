package com.example.itemmanagement.dto;

import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.entity.ShoppingListItem;

import java.util.List;

public class ShoppingListViewModel {

    private final List<ShoppingListItem> listItems;
    private final List<Categories> categories;
    private final List<Items> favoriteItems;

    public ShoppingListViewModel(
            List<ShoppingListItem> listItems,
            List<Categories> categories,
            List<Items> favoriteItems) {

        this.listItems = listItems;
        this.categories = categories;
        this.favoriteItems = favoriteItems;

    }

    public List<ShoppingListItem> getListItems() {
        return listItems;
    }

    public List<Categories> getCategories() {
        return categories;
    }

    public List<Items> getFavoriteItems() {
        return favoriteItems;
    }
}
