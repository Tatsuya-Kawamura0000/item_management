package com.example.itemmanagement.controller;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.form.AddItemForm;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.AddItemService;
import com.example.itemmanagement.service.UpdateItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shoppingList")
public class ShoppingListRestController {

    @Autowired
    private AddItemService addItemService;
    
    @Autowired
    private UpdateItemService  updateItemService;
    @PostMapping("/{id}/move-to-items")
    public Items addToItemList(
            @RequestBody AddItemForm form,
            @AuthenticationPrincipal LoginUser loginUser) {

        Integer userId = loginUser.getId();

        Items savedItem = addItemService.addAndReturn(form, userId);

        addItemService.deleteFromShoppingList(form.getId(), userId);

        return savedItem;
    }

    
    @PostMapping("/bulk-delete")
    public void bulkDeleteShoppingList(
            @RequestBody List<Integer> ids,
            @AuthenticationPrincipal LoginUser loginUser){

        Integer userId = loginUser.getId();

        updateItemService.bulkDelete(ids, userId);
    }
	

}