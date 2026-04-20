package com.example.itemmanagement.controller;

import com.example.itemmanagement.dto.ShoppingListViewModel;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.AddToShoppingListService;
import com.example.itemmanagement.service.ShoppingListBulkService;
import com.example.itemmanagement.service.ShoppingListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/shoppingList")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;
    private final AddToShoppingListService addToShoppingListService;
    private final ShoppingListBulkService shoppingListBulkService;

    public ShoppingListController(
            ShoppingListService shoppingListService,
            AddToShoppingListService addToShoppingListService,
            ShoppingListBulkService shoppingListBulkService) {

        this.shoppingListService = shoppingListService;
        this.addToShoppingListService = addToShoppingListService;
        this.shoppingListBulkService = shoppingListBulkService;
    }


    @GetMapping
    public String view(Model model,
                       @AuthenticationPrincipal LoginUser user) {

        ShoppingListViewModel slvm = shoppingListService.getPageData(user.getId());

        model.addAttribute("slvm", slvm);

        return "shoppingList";
    }

    @PostMapping("/add-to-shopping-list/{id}")
    public String addToShoppingList(
            @PathVariable("id") int id,
            @AuthenticationPrincipal LoginUser loginUser,
            RedirectAttributes redirectAttributes) {

        Integer userId = loginUser.getId();

        // サービス呼び出しでShoppingListに追加
        addToShoppingListService.addItemToList(id, userId);

        // フラッシュメッセージ
        redirectAttributes.addFlashAttribute("successMessage", "買い物リストに追加しました！");

        return "redirect:/users";
    }

    @PostMapping("/bulk-add-shopping-list")
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
                    + " はすでに買い物リストに存在しています";

            return ResponseEntity
                    .badRequest()
                    .body(message);
        }

        return ResponseEntity.ok().build();
    }

}

