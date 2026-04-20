package com.example.itemmanagement.controller;

import com.example.itemmanagement.entity.ShoppingListItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.itemmanagement.form.AddShoppingListItemForm;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.AddToShoppingListService;

@RestController
@RequestMapping("/shoppingList")
public class AddNewItemController {

    @Autowired
    private AddToShoppingListService addToShoppingListService;

    @PostMapping("/add-new")
    public ShoppingListItem addNewItemToShoppingList(
            @RequestBody AddShoppingListItemForm form,
            @AuthenticationPrincipal LoginUser loginUser) {

        Integer userId = loginUser.getId();

        return addToShoppingListService.addNewItem(form, userId); // ★返す
    }

}