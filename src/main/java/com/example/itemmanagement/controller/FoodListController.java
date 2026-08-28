package com.example.itemmanagement.controller;

import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.CategoryService;
import com.example.itemmanagement.service.ItemQueryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class FoodListController {

    private final ItemQueryService itemQueryService;
    private final CategoryService categoryService;

    public FoodListController(ItemQueryService itemQueryService, CategoryService categoryService) {
        this.itemQueryService = itemQueryService;
        this.categoryService = categoryService;
    }

    @GetMapping("/foods")
    public String index(Model model, @AuthenticationPrincipal LoginUser user) {
        Integer userId = user != null ? user.getId() : null;

        List<Items> items = itemQueryService.getSourceItems(userId);
        model.addAttribute("items", items);

        List<Categories> categories = categoryService.getAllCategories();
        Map<Integer, Integer> categoryCounts = categoryService.getCategoryCounts(userId);

        model.addAttribute("categories", categories);
        model.addAttribute("categoryCounts", categoryCounts);
        model.addAttribute("totalCount", items != null ? items.size() : 0);

        return "index";
    }
}