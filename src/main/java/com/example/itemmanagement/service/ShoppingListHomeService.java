package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.ShoppingListViewModel;
import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.entity.ShoppingListItem;
import com.example.itemmanagement.mapper.ShoppingListMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ShoppingListHomeService {

    private final ShoppingListMapper shoppingListMapper;
    private final CategoryService categoryService;

    public ShoppingListHomeService(ShoppingListMapper shoppingListMapper, CategoryService categoryService) {

        this.shoppingListMapper = shoppingListMapper;
        this.categoryService = categoryService;
    }

    public ShoppingListViewModel getPageData(Integer userId) {

        List<ShoppingListItem> list = shoppingListMapper.findAll(userId);
        List<Categories> categories = categoryService.getAllCategories();

        return new ShoppingListViewModel(list, categories);
    }

}

