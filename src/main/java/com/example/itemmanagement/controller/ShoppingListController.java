package com.example.itemmanagement.controller;

import com.example.itemmanagement.dto.ShoppingListViewModel;
import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.AddToShoppingListService;
import com.example.itemmanagement.service.ItemQueryService;
import com.example.itemmanagement.service.ShoppingListBulkService;
import com.example.itemmanagement.service.ShoppingListHomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/shopping-list")
public class ShoppingListController {

    private final ShoppingListHomeService shoppingListHomeService;
    private final ItemQueryService itemQueryService;

    public ShoppingListController(
            ShoppingListHomeService shoppingListService,
            AddToShoppingListService addToShoppingListService,
            ShoppingListBulkService shoppingListBulkService,
            ItemQueryService itemQueryService) {

        this.shoppingListHomeService = shoppingListService;
        this.itemQueryService = itemQueryService;
    }


    @GetMapping
    public String view(
            Model model,
            @AuthenticationPrincipal LoginUser user) {

        ShoppingListViewModel slvm =
                shoppingListHomeService.getPageData(user.getId());

        model.addAttribute("slvm", slvm);

        return "shoppingList";
    }

    @GetMapping("/api/foods")
    @ResponseBody
    public ResponseEntity<java.util.List<com.example.itemmanagement.dto.RecentPurchaseGroupDto>> getFoodItems(@AuthenticationPrincipal LoginUser user) {

        Integer userId = user.getId();

        // 直近3回分の購入日ごとのグルーピング DTO を返却する
        java.util.List<com.example.itemmanagement.dto.RecentPurchaseGroupDto> groups = itemQueryService.getRecentPurchaseGroups(userId);

        return ResponseEntity.ok(groups);
    }
}