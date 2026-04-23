package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.RecipeResponse;
import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.entity.Recipe;
import com.example.itemmanagement.mapper.RecipeMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor //これが final フィールドを引数に持つコンストラクタを自動生成します
public class RecipeService {

    private final RecipeMapper recipeMapper;
    private final ObjectMapper objectMapper;

    public void saveRecipe(Integer userId, RecipeResponse response, List<Items> sourceItems) {

        try {
            // ① Entity作成
            Recipe recipe = new Recipe();

            recipe.setUserId(userId);
            recipe.setRecipeName(response.getRecipeName());
            recipe.setDescription(response.getDescription());
            recipe.setFavorite(false);
            recipe.setStatus(0);
            recipe.setCreatedAt(LocalDateTime.now());

            // ② List → JSON変換
            recipe.setIngredientsJson(objectMapper.writeValueAsString(response.getIngredients()));
            recipe.setStepsJson(objectMapper.writeValueAsString(response.getSteps()));
            recipe.setSourceItemsJson(objectMapper.writeValueAsString(sourceItems));

            // ③ DB INSERT
            recipeMapper.insertRecipe(recipe);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("レシピ保存に失敗しました");
        }
    }


    //レシピ取得用(初期画面)
    public List<Recipe>  getRecipe(Integer userId) {

        return recipeMapper.selectRecipe(userId);

    }


}
