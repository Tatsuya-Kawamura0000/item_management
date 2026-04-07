package com.example.itemmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.form.AddItemForm;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.AddItemService;
import com.example.itemmanagement.service.UpdateItemService;

@RestController 
public class ShoppingListRestController {

    @Autowired
    private AddItemService addItemService;
    
    @Autowired
    private UpdateItemService  updateItemService;

    @PostMapping("/users/add-to-shopping-list")
    public Items addToItemList(
            @RequestBody AddItemForm form,
            @AuthenticationPrincipal LoginUser loginUser) {

        Integer userId = loginUser.getId();

        Items savedItem = addItemService.addAndReturn(form, userId);
        
        addItemService.deleteFromShoppingList(form.getId(), userId);

        return savedItem;
    }
    
    @PostMapping("/users/bulk-delete-shopping-list")
    public void bulkDeleteShoppingList(
            @RequestBody List<Integer> ids,
            @AuthenticationPrincipal LoginUser loginUser){

        Integer userId = loginUser.getId();

        updateItemService.bulkDelete(ids, userId);
    }
	

}