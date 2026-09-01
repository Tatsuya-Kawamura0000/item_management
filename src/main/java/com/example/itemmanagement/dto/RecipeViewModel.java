package com.example.itemmanagement.dto;

import com.example.itemmanagement.entity.Recipe;
import com.example.itemmanagement.entity.RecipeCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeViewModel {

    private List<Recipe> recipes = Collections.emptyList();
    private List<RecipeCategory> categories = Collections.emptyList();
    private Map<Integer, Long> categoryCountMap = Collections.emptyMap();
    private int totalRecipeCount = 0;

    // 後方互換用コンストラクタ
    public RecipeViewModel(List<Recipe> recipes) {
        this.recipes = recipes != null ? recipes : Collections.emptyList();
        this.totalRecipeCount = this.recipes.size();
    }

    // 後方互換用ゲッター
    public List<Recipe> getRecipe() {
        return recipes;
    }
}