package com.example.itemmanagement.controller;

import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.FavoriteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }


    @PostMapping("/{id}")
    public String toggleFavorite(
            @PathVariable("id") int id,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) Boolean expiringSoon,
            @AuthenticationPrincipal LoginUser loginUser) {

        Integer userId = loginUser.getId();

        // ★ 業務ロジックはServiceへ
        favoriteService.toggleFavorite(id, userId);

        // フィルター条件がある場合
        if (category != null || expiringSoon != null) {

            StringBuilder url = new StringBuilder("redirect:/users/filter?");

            if (category != null) url.append("category=").append(category).append("&");
            if (expiringSoon != null && expiringSoon) url.append("expiringSoon=true");

            return url.toString();
        }

        return "redirect:/users";
    }
}
