package com.example.itemmanagement.controller;

import com.example.itemmanagement.dto.ShoppingListViewModel;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.AddToShoppingListService;
import com.example.itemmanagement.service.ShoppingListBulkService;
import com.example.itemmanagement.service.ShoppingListHomeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/shoppingList")
public class ShoppingListController {

    private final ShoppingListHomeService shoppingListHomeService;

    public ShoppingListController(
            ShoppingListHomeService shoppingListService,
            AddToShoppingListService addToShoppingListService,
            ShoppingListBulkService shoppingListBulkService) {

        this.shoppingListHomeService = shoppingListService;
    }


    @GetMapping
    public String view(Model model,
                       @AuthenticationPrincipal LoginUser user) {

        ShoppingListViewModel slvm = shoppingListHomeService.getPageData(user.getId());

        model.addAttribute("slvm", slvm);

        return "shoppingList";
    }


}

