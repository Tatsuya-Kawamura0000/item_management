package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.RecipeViewModel;
import com.example.itemmanagement.entity.Recipe;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeHomeService {

    //コンストラクタインジェクション
    private final RecipeService recipeService;

    public RecipeHomeService(RecipeService recipeService) {
        this.recipeService = recipeService;
    }


    public RecipeViewModel getRecipeHomeData(Integer userId) {

        //レシピを取得し、格納
        List<Recipe> recipe = recipeService.getRecipe(userId);

        return new RecipeViewModel(recipe);

    }

}
