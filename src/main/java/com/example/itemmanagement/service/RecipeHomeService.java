package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.RecipeViewModel;
import com.example.itemmanagement.entity.Recipe;
import com.example.itemmanagement.entity.RecipeCategory;
import com.example.itemmanagement.entity.RecipeIngredient;
import com.example.itemmanagement.entity.Items;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Locale;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeHomeService {

    private final RecipeService recipeService;
    private final ItemQueryService itemQueryService;

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

    /**
     * 今夜の献立用に、主要食材がすべて在庫中（status = 1）のレシピだけを返す。
     * 主要食材が未設定のレシピは、在庫で判定できないため候補に含めない。
     */
    public List<Recipe> getRecipesAvailable(Integer userId) {
        Set<String> availableIngredientNames = itemQueryService.getSourceItems(userId).stream()
                .map(Items::getName)
                .map(this::normalizeIngredientName)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toSet());

        return recipeService.getAllRecipesWithDetails(userId).stream()
                .filter(recipe -> hasAllMainIngredients(recipe, availableIngredientNames))
                .collect(Collectors.toList());
    }

    private boolean hasAllMainIngredients(Recipe recipe, Set<String> availableIngredientNames) {
        List<RecipeIngredient> mainIngredients = recipe.getIngredients() == null
                ? Collections.emptyList()
                : recipe.getIngredients().stream()
                .filter(ingredient -> Boolean.TRUE.equals(ingredient.getIsMain()))
                .collect(Collectors.toList());

        return !mainIngredients.isEmpty() && mainIngredients.stream()
                .map(RecipeIngredient::getIngredientName)
                .map(this::normalizeIngredientName)
                .allMatch(name -> !name.isEmpty() && availableIngredientNames.contains(name));
    }

    private String normalizeIngredientName(String name) {
        return name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
    }
}
