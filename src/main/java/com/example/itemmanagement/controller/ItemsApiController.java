package com.example.itemmanagement.controller;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.form.AddItemForm;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.AddItemService;
import com.example.itemmanagement.service.ItemQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
public class ItemsApiController {

    @Autowired
    private ItemQueryService itemQueryService;

    @Autowired
    private AddItemService addItemService;

    @GetMapping("/suggest")
    public List<Items> suggestItems(@RequestParam(required = false) String keyword,
                                    @AuthenticationPrincipal LoginUser loginUser) {

        Integer userId = loginUser.getId();

        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<Items> results = itemQueryService.search("name", keyword.trim(), userId);

        // 購入日が最新のデータを取得する
        List<Items> sorted = results.stream()
                .sorted(Comparator.comparing(Items::getPurchaseDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        Map<String, Items> map = new LinkedHashMap<>();
        for (Items it : sorted) {
            String name = it.getName();
            if (!map.containsKey(name)) {
                map.put(name, it);
            }
        }

        return new ArrayList<>(map.values());
    }

    @PostMapping("/create")
    public Items createItem(@RequestBody AddItemForm form, @AuthenticationPrincipal LoginUser loginUser) {
        Integer userId = loginUser.getId();
        return addItemService.add(form, userId);
    }

}
