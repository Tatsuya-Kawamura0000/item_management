package com.example.itemmanagement.controller;

import com.example.itemmanagement.dto.RecipeResponse;
import com.example.itemmanagement.security.LoginUser;
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

    //private final GeminiService geminiService;
    private final OpenAiService openAiService;
    private final RecipeService recipeService;

    @GetMapping("/test-recipe")
    public RecipeResponse testRecipe(@AuthenticationPrincipal LoginUser loginUser) {

        List<String> items = List.of("卵", "玉ねぎ", "鶏肉");

        //RecipeResponse response = geminiService.getRecipeSuggestion(items);

        RecipeResponse response = openAiService.getRecipeSuggestion(items);

        Integer userId = loginUser.getId();

        recipeService.saveRecipe(userId, response, items);

        return response;
    }
}
