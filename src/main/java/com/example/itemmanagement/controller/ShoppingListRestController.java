package com.example.itemmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.form.AddItemForm;
import com.example.itemmanagement.form.AddShoppingListItemForm;
import com.example.itemmanagement.service.AddItemService;
import com.example.itemmanagement.service.AddToShoppingListService;

@Controller 
public class ShoppingListRestController {
	
	@Autowired
	private  AddItemService addItemService;
	
	@Autowired
    private AddToShoppingListService addToShoppingListService;

	@PostMapping("/users/add-to-shopping-list")
    public Items addToItemList(@RequestBody AddItemForm form) {
        // items に追加
        Items savedItem = addItemService.addAndReturn(form);

        // shopping_list から削除
        addItemService.deleteFromShoppingList(form.getId());

        return savedItem; // JS に返す
    }
	
	@PostMapping("/users/add-new-item-to-shopping-list")
	public String addNewItemToShoppingList(@RequestParam String name,
	                                       @RequestParam String amount,
	                                       Model model) {
		AddShoppingListItemForm item = new AddShoppingListItemForm();
	    item.setName(name);
	    item.setAmount(amount);


	    addToShoppingListService.addNewItem(item); // Mapper 経由でDBに保存

	    return "redirect:/users/shoppingList"; // ページリロードして反映
	}

}