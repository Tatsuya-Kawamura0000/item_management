package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.RecipeViewModel;
import com.example.itemmanagement.entity.Recipe;
import com.example.itemmanagement.entity.RecipeCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeHomeService {

    private final RecipeService recipeService;

    public RecipeViewModel getRecipeHomeData(Integer userId) {
        // ユーザーの全レシピ（材料・手順含む）を取得
        List<Recipe> recipes = recipeService.getAllRecipesWithDetails(userId);

        // カテゴリマスタ一覧を取得
        List<RecipeCategory> categories = recipeService.getAllRecipeCategories();

        // カテゴリごとの件数集計
        Map<Integer, Long> categoryCountMap = recipes.stream()
                .filter(r -> r.getCategoryId() != null)
                .collect(Collectors.groupingBy(Recipe::getCategoryId, Collectors.counting()));

        return new RecipeViewModel(recipes, categories, categoryCountMap, recipes.size());
    }
}
