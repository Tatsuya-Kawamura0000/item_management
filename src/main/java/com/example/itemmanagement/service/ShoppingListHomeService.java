package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.ShoppingListViewModel;
import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.entity.ShoppingListItem;
import com.example.itemmanagement.mapper.ShoppingListMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ShoppingListHomeService {

    private final ShoppingListMapper shoppingListMapper;
    private final CategoryService categoryService;
    private final ItemQueryService itemQueryService;

    public ShoppingListHomeService(ShoppingListMapper shoppingListMapper, CategoryService categoryService, ItemQueryService itemQueryService) {

        this.shoppingListMapper = shoppingListMapper;
        this.categoryService = categoryService;
        this.itemQueryService = itemQueryService;
    }

    public ShoppingListViewModel getPageData(Integer userId) {

        List<ShoppingListItem> list = shoppingListMapper.findAll(userId);
        List<Categories> categories = categoryService.getAllCategories();
        List<Items> favoriteItems =  itemQueryService.getFavoriteItems(userId);


        return new ShoppingListViewModel(list, categories, favoriteItems);
    }

}

