package com.example.itemmanagement.controller;

import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.ShoppingListBulkService;
import com.example.itemmanagement.service.ShoppingListHomeService;
import com.example.itemmanagement.service.UpdateItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemBulkController {

    private final UpdateItemService updateItemService;
    private final ShoppingListBulkService shoppingListBulkService;

    public ItemBulkController(UpdateItemService updateItemService, ShoppingListHomeService shoppingListHomeService, ShoppingListBulkService shoppingListBulkService) {
        this.updateItemService = updateItemService;
        this.shoppingListBulkService = shoppingListBulkService;
    }

    @PostMapping("/bulk-stop")
    @ResponseBody
    public ResponseEntity<?> bulkStop(@RequestBody List<Integer> ids,
                                     @AuthenticationPrincipal LoginUser loginUser){

        Integer userId = loginUser.getId();

        updateItemService.bulkStopFromItems(ids, userId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk-delete")
    @ResponseBody
    public ResponseEntity<?> bulkDelete(@RequestBody List<Integer> ids,
                                        @AuthenticationPrincipal LoginUser loginUser){

        Integer userId = loginUser.getId();

        updateItemService.bulkDeleteFromItems(ids, userId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateItemAjax(@PathVariable("id") int id,
                                            @RequestBody com.example.itemmanagement.entity.Items item,
                                            @AuthenticationPrincipal LoginUser loginUser) {

        Integer userId = loginUser.getId();

        updateItemService.updateItem(id, userId, item);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk-add")
    @ResponseBody
    public ResponseEntity<?> bulkAddShoppingList(
            @RequestBody List<Integer> ids,
            @AuthenticationPrincipal LoginUser loginUser){

        Integer userId = loginUser.getId();

        //重複していたアイテムのリスト
        List<String> duplicatedItems = shoppingListBulkService.addAll(ids,userId);

        // 重複が1つでもあればまとめて返す
        if(!duplicatedItems.isEmpty()){
            String message = String.join("、", duplicatedItems)
                    + " はすでに買い物リストに追加しています";

            return ResponseEntity
                    .badRequest()
                    .body(message);
        }

        return ResponseEntity.ok().build();
    }
}
