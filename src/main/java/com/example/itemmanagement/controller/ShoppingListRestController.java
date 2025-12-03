package com.example.itemmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.form.AddItemForm;
import com.example.itemmanagement.service.AddItemService;

@RestController 
public class ShoppingListRestController {
	
	@Autowired
	private  AddItemService addItemService;

	@PostMapping("/users/add-to-shopping-list")
    public Items addToItemList(@RequestBody AddItemForm form) {
        // items に追加
        Items savedItem = addItemService.addAndReturn(form);

        // shopping_list から削除
        addItemService.deleteFromShoppingList(form.getId());

        return savedItem; // JS に返す
    }
	

}