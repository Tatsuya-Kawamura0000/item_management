package com.example.itemmanagement.controller;

import com.example.itemmanagement.dto.ShoppingListViewModel;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.AddToShoppingListService;
import com.example.itemmanagement.service.ShoppingListBulkService;
import com.example.itemmanagement.service.ShoppingListService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/shoppingList")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    public ShoppingListController(
            ShoppingListService shoppingListService,
            AddToShoppingListService addToShoppingListService,
            ShoppingListBulkService shoppingListBulkService) {

        this.shoppingListService = shoppingListService;
    }


    @GetMapping
    public String view(Model model,
                       @AuthenticationPrincipal LoginUser user) {

        ShoppingListViewModel slvm = shoppingListService.getPageData(user.getId());

        model.addAttribute("slvm", slvm);

        return "shoppingList";
    }


}

