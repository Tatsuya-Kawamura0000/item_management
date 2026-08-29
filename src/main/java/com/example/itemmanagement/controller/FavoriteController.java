package com.example.itemmanagement.controller;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.FavoriteService;
import com.example.itemmanagement.service.ItemQueryService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/items")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final ItemQueryService itemQueryService;

    public FavoriteController(FavoriteService favoriteService, ItemQueryService itemQueryService) {
        this.favoriteService = favoriteService;
        this.itemQueryService = itemQueryService;
    }

    @PostMapping("/favorite/{id}")
    public String toggleFavorite(
            @PathVariable("id") int id,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) Boolean expiringSoon,
            @AuthenticationPrincipal LoginUser loginUser) {

        Integer userId = loginUser.getId();

        favoriteService.toggleFavorite(id, userId);

        if (category != null || expiringSoon != null) {
            StringBuilder url = new StringBuilder("redirect:/users/filter?");

            if (category != null) url.append("category=").append(category).append("&");
            if (expiringSoon != null && expiringSoon) url.append("expiringSoon=true");

            return url.toString();
        }

        return "redirect:/";
    }

    @PostMapping(value = "/favorite-toggle/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> toggleFavoriteAjax(
            @PathVariable("id") int id,
            @AuthenticationPrincipal LoginUser loginUser) {

        Integer userId = loginUser != null ? loginUser.getId() : null;
        favoriteService.toggleFavorite(id, userId);

        Items item = itemQueryService.getItemById(id, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("favorite", item != null && item.isFavorite());
        result.put("id", id);
        return result;
    }
}
