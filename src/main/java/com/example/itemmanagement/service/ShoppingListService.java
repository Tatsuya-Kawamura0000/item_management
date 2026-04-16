package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.ShoppingListViewModel;
import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.entity.ShoppingListItem;
import com.example.itemmanagement.mapper.ShoppingListMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ShoppingListService {

    private final ShoppingListMapper shoppingListMapper;
    private final GetAllCategoriesService categoriesService;

    public ShoppingListService(
            ShoppingListMapper shoppingListMapper,
            GetAllCategoriesService categoriesService) {

        this.shoppingListMapper = shoppingListMapper;
        this.categoriesService = categoriesService;
    }

    public ShoppingListViewModel getPageData(Integer userId) {

        List<ShoppingListItem> list = shoppingListMapper.findAll(userId);
        List<Categories> categories = categoriesService.getAllCategories();

        return new ShoppingListViewModel(list, categories);
    }

}

