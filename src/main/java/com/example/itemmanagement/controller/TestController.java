/*package com.example.itemmanagement.controller;

import com.example.itemmanagement.dto.RecipeResponse;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.GetAllItemsService;
import com.example.itemmanagement.service.OpenAiService;
import com.example.itemmanagement.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final OpenAiService openAiService;
    private final RecipeService recipeService;
    private final GetAllItemsService getAllItemsService;

    @GetMapping("/test-recipe")
    public RecipeResponse testRecipe(@AuthenticationPrincipal LoginUser loginUser) {

        //List<String> items = List.of("卵", "玉ねぎ", "鶏肉");

        // ログインユーザーID情報格納
        Integer userId = loginUser.getId();

        //名前のみ取得し、格納
        List<String> sourceItems = getAllItemsService.getSourceItems(userId);

        System.out.println(sourceItems);

        RecipeResponse response = openAiService.getRecipeSuggestion(sourceItems);


        recipeService.saveRecipe(userId, response, sourceItems);

        return response;
    }
}
*/