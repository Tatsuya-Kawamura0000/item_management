package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.RecipeResponse;
import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.entity.Recipe;
import com.example.itemmanagement.entity.RecipeCategory;
import com.example.itemmanagement.entity.RecipeIngredient;
import com.example.itemmanagement.entity.RecipeStep;
import com.example.itemmanagement.form.RecipeCreateForm;
import com.example.itemmanagement.mapper.RecipeCategoryMapper;
import com.example.itemmanagement.mapper.RecipeIngredientMapper;
import com.example.itemmanagement.mapper.RecipeMapper;
import com.example.itemmanagement.mapper.RecipeStepMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeMapper recipeMapper;
    private final RecipeCategoryMapper recipeCategoryMapper;
    private final RecipeIngredientMapper recipeIngredientMapper;
    private final RecipeStepMapper recipeStepMapper;

    /**
     * レシピカテゴリ一覧を取得
     */
    public List<RecipeCategory> getAllRecipeCategories() {
        return recipeCategoryMapper.findAll();
    }

    /**
     * 画面から入力されたレシピを新規登録する
     */
    @Transactional
    public Integer createRecipe(Integer userId, RecipeCreateForm form) {
        // ① レシピ本体の作成・登録
        Recipe recipe = new Recipe();
        recipe.setUserId(userId);
        recipe.setRecipeName(form.getRecipeName());
        recipe.setServings(form.getServings() != null ? form.getServings() : 2);
        recipe.setCategoryId(form.getCategoryId());
        recipe.setSourceType(form.getSourceType() != null && !form.getSourceType().isBlank() ? form.getSourceType() : "USER");
        recipe.setSourceUrl(form.getSourceUrl());
        recipe.setMemo(form.getMemo());
        recipe.setIsFavorite(false);
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setUpdatedAt(LocalDateTime.now());

        recipeMapper.insertRecipe(recipe);
        Integer recipeId = recipe.getId();
        log.info("[RecipeService] recipesテーブルに保存完了 ID: {}", recipeId);

        // ② 材料の登録
        if (form.getIngredients() != null && !form.getIngredients().isEmpty()) {
            List<RecipeIngredient> ingredientList = new ArrayList<>();
            int order = 1;
            for (RecipeCreateForm.IngredientForm ingForm : form.getIngredients()) {
                if (ingForm.getIngredientName() != null && !ingForm.getIngredientName().isBlank()) {
                    RecipeIngredient ingredient = new RecipeIngredient();
                    ingredient.setRecipeId(recipeId);
                    ingredient.setIngredientName(ingForm.getIngredientName().trim());
                    ingredient.setQuantity(ingForm.getQuantity());
                    ingredient.setUnit(ingForm.getUnit());
                    ingredient.setIsMain(ingForm.getIsMain() != null ? ingForm.getIsMain() : false);
                    ingredient.setDisplayOrder(order++);
                    ingredient.setCategoryId(ingForm.getCategoryId());
                    ingredientList.add(ingredient);
                }
            }
            if (!ingredientList.isEmpty()) {
                recipeIngredientMapper.batchInsert(ingredientList);
                log.info("[RecipeService] recipe_ingredientテーブルに {} 件の材料を保存完了", ingredientList.size());
            } else {
                log.warn("[RecipeService] 有効な材料名が入力されていないため材料保存をスキップしました");
            }
        } else {
            log.warn("[RecipeService] 材料リストが空またはnullです");
        }

        // ③ 手順の登録
        if (form.getSteps() != null && !form.getSteps().isEmpty()) {
            List<RecipeStep> stepList = new ArrayList<>();
            int stepNo = 1;
            for (RecipeCreateForm.StepForm stepForm : form.getSteps()) {
                if (stepForm.getInstruction() != null && !stepForm.getInstruction().isBlank()) {
                    RecipeStep step = new RecipeStep();
                    step.setRecipeId(recipeId);
                    step.setStepNo(stepNo++);
                    step.setInstruction(stepForm.getInstruction().trim());
                    stepList.add(step);
                }
            }
            if (!stepList.isEmpty()) {
                recipeStepMapper.batchInsert(stepList);
                log.info("[RecipeService] recipe_stepテーブルに {} 件の手順を保存完了", stepList.size());
            }
        }

        return recipeId;
    }

    /**
     * AI提案レシピの保存（新DB構造に対応）
     */
    @Transactional
    public Integer saveRecipe(Integer userId, RecipeResponse response, List<Items> sourceItems) {
        Recipe recipe = new Recipe();
        recipe.setUserId(userId);
        recipe.setRecipeName(response.getRecipeName());
        recipe.setServings(2); // デフォルト2人分
        recipe.setMemo(response.getDescription());
        recipe.setSourceType("AI");
        recipe.setIsFavorite(false);
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setUpdatedAt(LocalDateTime.now());

        recipeMapper.insertRecipe(recipe);
        Integer recipeId = recipe.getId();

        // 材料登録
        if (response.getIngredients() != null && !response.getIngredients().isEmpty()) {
            List<RecipeIngredient> ingredientList = new ArrayList<>();
            int order = 1;
            for (String ingName : response.getIngredients()) {
                if (ingName != null && !ingName.isBlank()) {
                    RecipeIngredient ingredient = new RecipeIngredient();
                    ingredient.setRecipeId(recipeId);
                    ingredient.setIngredientName(ingName.trim());
                    ingredient.setIsMain(false);
                    ingredient.setDisplayOrder(order++);
                    ingredientList.add(ingredient);
                }
            }
            if (!ingredientList.isEmpty()) {
                recipeIngredientMapper.batchInsert(ingredientList);
            }
        }

        // 手順登録
        if (response.getSteps() != null && !response.getSteps().isEmpty()) {
            List<RecipeStep> stepList = new ArrayList<>();
            int stepNo = 1;
            for (String stepText : response.getSteps()) {
                if (stepText != null && !stepText.isBlank()) {
                    RecipeStep step = new RecipeStep();
                    step.setRecipeId(recipeId);
                    step.setStepNo(stepNo++);
                    step.setInstruction(stepText.trim());
                    stepList.add(step);
                }
            }
            if (!stepList.isEmpty()) {
                recipeStepMapper.batchInsert(stepList);
            }
        }

        return recipeId;
    }

    /**
     * レシピ詳細取得（材料・手順含む）
     */
     public Recipe getRecipeDetail(Integer recipeId) {
         Recipe recipe = recipeMapper.findById(recipeId);
         if (recipe != null) {
             recipe.setIngredients(recipeIngredientMapper.selectByRecipeId(recipeId));
             recipe.setSteps(recipeStepMapper.selectByRecipeId(recipeId));
         }
         return recipe;
     }

    /**
     * ユーザーの全レシピを取得し、材料・手順もセットする
     */
    public List<Recipe> getAllRecipesWithDetails(Integer userId) {
        List<Recipe> recipes = recipeMapper.selectByUserId(userId);
        for (Recipe r : recipes) {
            r.setIngredients(recipeIngredientMapper.selectByRecipeId(r.getId()));
            r.setSteps(recipeStepMapper.selectByRecipeId(r.getId()));
        }
        return recipes;
    }

    /**
     * レシピの更新処理
     */
    @Transactional
    public void updateRecipe(Integer userId, Integer recipeId, RecipeCreateForm form) {
        Recipe recipe = recipeMapper.findById(recipeId);
        if (recipe == null || !recipe.getUserId().equals(userId)) {
            throw new IllegalArgumentException("指定されたレシピが存在しないか、権限がありません。");
        }

        // ① レシピ本体の更新
        recipe.setRecipeName(form.getRecipeName());
        recipe.setServings(form.getServings() != null ? form.getServings() : 2);
        recipe.setCategoryId(form.getCategoryId());
        recipe.setSourceUrl(form.getSourceUrl());
        recipe.setMemo(form.getMemo());
        recipeMapper.updateRecipe(recipe);

        // ② 材料の再登録
        recipeIngredientMapper.deleteByRecipeId(recipeId);
        if (form.getIngredients() != null && !form.getIngredients().isEmpty()) {
            List<RecipeIngredient> ingredientList = new ArrayList<>();
            int order = 1;
            for (RecipeCreateForm.IngredientForm ingForm : form.getIngredients()) {
                if (ingForm.getIngredientName() != null && !ingForm.getIngredientName().isBlank()) {
                    RecipeIngredient ingredient = new RecipeIngredient();
                    ingredient.setRecipeId(recipeId);
                    ingredient.setIngredientName(ingForm.getIngredientName().trim());
                    ingredient.setQuantity(ingForm.getQuantity());
                    ingredient.setUnit(ingForm.getUnit());
                    ingredient.setIsMain(ingForm.getIsMain() != null ? ingForm.getIsMain() : false);
                    ingredient.setDisplayOrder(order++);
                    ingredient.setCategoryId(ingForm.getCategoryId());
                    ingredientList.add(ingredient);
                }
            }
            if (!ingredientList.isEmpty()) {
                recipeIngredientMapper.batchInsert(ingredientList);
            }
        }

        // ③ 手順の再登録
        recipeStepMapper.deleteByRecipeId(recipeId);
        if (form.getSteps() != null && !form.getSteps().isEmpty()) {
            List<RecipeStep> stepList = new ArrayList<>();
            int stepNo = 1;
            for (RecipeCreateForm.StepForm stepForm : form.getSteps()) {
                if (stepForm.getInstruction() != null && !stepForm.getInstruction().isBlank()) {
                    RecipeStep step = new RecipeStep();
                    step.setRecipeId(recipeId);
                    step.setStepNo(stepNo++);
                    step.setInstruction(stepForm.getInstruction().trim());
                    stepList.add(step);
                }
            }
            if (!stepList.isEmpty()) {
                recipeStepMapper.batchInsert(stepList);
            }
        }
        log.info("[RecipeService] レシピ更新完了 ID: {}", recipeId);
    }

    /**
     * レシピの削除処理
     */
    @Transactional
    public void deleteRecipe(Integer userId, Integer recipeId) {
        Recipe recipe = recipeMapper.findById(recipeId);
        if (recipe == null || !recipe.getUserId().equals(userId)) {
            throw new IllegalArgumentException("指定されたレシピが存在しないか、権限がありません。");
        }

        recipeIngredientMapper.deleteByRecipeId(recipeId);
        recipeStepMapper.deleteByRecipeId(recipeId);
        recipeMapper.deleteByIdAndUserId(recipeId, userId);
        log.info("[RecipeService] レシピ削除完了 ID: {}", recipeId);
    }

    /**
     * レシピ取得用(初期画面: 最新1件)
     */
    public List<Recipe> getRecipe(Integer userId) {
        List<Recipe> recipes = recipeMapper.selectRecipe(userId);
        for (Recipe r : recipes) {
            r.setIngredients(recipeIngredientMapper.selectByRecipeId(r.getId()));
            r.setSteps(recipeStepMapper.selectByRecipeId(r.getId()));
        }
        return recipes;
    }

    /**
     * 直近30件の過去レシピ取得用
     */
    public List<Recipe> findByUserId(Integer userId) {
        return recipeMapper.selectByUserId(userId);
    }
}
