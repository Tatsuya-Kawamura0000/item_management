package com.example.itemmanagement.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingListBulkService {

    private final AddToShoppingListService addToShoppingListService;

    public ShoppingListBulkService(AddToShoppingListService addToShoppingListService) {
        this.addToShoppingListService = addToShoppingListService;
    }


    public List<String> addAll(List<Integer> ids, Integer userId) {

        List<String> duplicatedItems = new ArrayList<>();

        for (Integer id : ids) {

            List<String> result = addToShoppingListService.addItemToList(id, userId);  //重複アイテムがあれば、返ってくる

            if (!result.isEmpty()) {
                duplicatedItems.addAll(result);  //重複アイテムをセット
            }
        }

        return duplicatedItems;   //重複アイテムを返す
    }
}
