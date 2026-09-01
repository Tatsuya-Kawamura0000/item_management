package com.example.itemmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    private Integer id;
    private Integer userId;
    private String recipeName;
    private Integer servings;
    private Integer categoryId; // レシピカテゴリID (recipe_category.id)
    private String sourceType;  // USER, AI, EXTERNAL
    private String sourceUrl;
    private String memo;
    private Boolean isFavorite;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 画面表示や結合取得用
    private String categoryName;
    private List<RecipeIngredient> ingredients;
    private List<RecipeStep> steps;

}
