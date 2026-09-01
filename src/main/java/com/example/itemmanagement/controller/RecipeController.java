package com.example.itemmanagement.controller;

import com.example.itemmanagement.dto.RecipeResponse;
import com.example.itemmanagement.dto.RecipeViewModel;
import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.entity.Recipe;
import com.example.itemmanagement.form.RecipeCreateForm;
import com.example.itemmanagement.mapper.CategoryMapper;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.ItemQueryService;
import com.example.itemmanagement.service.OpenAiService;
import com.example.itemmanagement.service.RecipeHomeService;
import com.example.itemmanagement.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {

    private final OpenAiService openAiService;
    private final RecipeService recipeService;
    private final ItemQueryService itemQueryService;
    private final RecipeHomeService recipeHomeService;
    private final CategoryMapper categoryMapper;

    // レシピ画面を返す
    @GetMapping
    public String recipePage(Model model,
                             @AuthenticationPrincipal LoginUser loginUser) {

        Integer userId = loginUser.getId();

        RecipeViewModel rvm = recipeHomeService.getRecipeHomeData(userId);
        model.addAttribute("rvm", rvm);

        // レシピ登録用フォームとマスタデータ
        if (!model.containsAttribute("recipeCreateForm")) {
            RecipeCreateForm form = new RecipeCreateForm();
            // 初期表示用に1行分の材料と手順を入れておく
            form.getIngredients().add(new RecipeCreateForm.IngredientForm());
            form.getSteps().add(new RecipeCreateForm.StepForm());
            model.addAttribute("recipeCreateForm", form);
        }
        model.addAttribute("recipeCategories", recipeService.getAllRecipeCategories());
        model.addAttribute("ingredientCategories", categoryMapper.findAll());

        return "recipe";
    }

    // 画面からのレシピ登録
    @PostMapping("/create")
    public String createRecipe(@AuthenticationPrincipal LoginUser loginUser,
                               @ModelAttribute("recipeCreateForm") @Valid RecipeCreateForm form,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        Integer userId = loginUser.getId();
        log.info("[RecipeCreate] 受信 - ユーザーID: {}, レシピ名: {}, 材料件数: {}, 手順件数: {}",
                userId, form.getRecipeName(),
                form.getIngredients() != null ? form.getIngredients().size() : 0,
                form.getSteps() != null ? form.getSteps().size() : 0);

        if (form.getIngredients() != null) {
            for (int i = 0; i < form.getIngredients().size(); i++) {
                RecipeCreateForm.IngredientForm ing = form.getIngredients().get(i);
                log.info("[RecipeCreate] 材料[{}]: name={}, qty={}, unit={}, isMain={}, catId={}",
                        i, ing.getIngredientName(), ing.getQuantity(), ing.getUnit(), ing.getIsMain(), ing.getCategoryId());
            }
        }

        if (bindingResult.hasErrors()) {
            log.warn("[RecipeCreate] バリデーションエラー発生: {}", bindingResult.getAllErrors());
            RecipeViewModel rvm = recipeHomeService.getRecipeHomeData(userId);
            model.addAttribute("rvm", rvm);
            model.addAttribute("recipeCategories", recipeService.getAllRecipeCategories());
            model.addAttribute("ingredientCategories", categoryMapper.findAll());
            model.addAttribute("showRegisterModal", true);
            return "recipe";
        }

        Integer recipeId = recipeService.createRecipe(userId, form);
        log.info("[RecipeCreate] 登録完了 - 生成レシピID: {}", recipeId);
        redirectAttributes.addFlashAttribute("successMessage", "レシピ「" + form.getRecipeName() + "」を登録しました");

        return "redirect:/recipes";
    }

    // レシピ編集
    @PostMapping("/edit/{id}")
    public String editRecipe(@PathVariable("id") Integer id,
                             @AuthenticationPrincipal LoginUser loginUser,
                             @ModelAttribute("recipeEditForm") @Valid RecipeCreateForm form,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        Integer userId = loginUser.getId();
        log.info("[RecipeEdit] 受信 - レシピID: {}, ユーザーID: {}, レシピ名: {}", id, userId, form.getRecipeName());

        if (bindingResult.hasErrors()) {
            log.warn("[RecipeEdit] バリデーションエラー発生: {}", bindingResult.getAllErrors());
            RecipeViewModel rvm = recipeHomeService.getRecipeHomeData(userId);
            model.addAttribute("rvm", rvm);
            model.addAttribute("recipeCategories", recipeService.getAllRecipeCategories());
            model.addAttribute("ingredientCategories", categoryMapper.findAll());
            model.addAttribute("editRecipeId", id);
            model.addAttribute("showEditModal", true);
            return "recipe";
        }

        try {
            recipeService.updateRecipe(userId, id, form);
            redirectAttributes.addFlashAttribute("successMessage", "レシピ「" + form.getRecipeName() + "」を更新しました");
        } catch (Exception e) {
            log.error("[RecipeEdit] 更新エラー: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "レシピの更新に失敗しました");
        }

        return "redirect:/recipes";
    }

    // レシピ削除
    @PostMapping("/delete/{id}")
    public String deleteRecipe(@PathVariable("id") Integer id,
                               @AuthenticationPrincipal LoginUser loginUser,
                               RedirectAttributes redirectAttributes) {

        Integer userId = loginUser.getId();
        log.info("[RecipeDelete] 削除要求 - レシピID: {}, ユーザーID: {}", id, userId);

        try {
            recipeService.deleteRecipe(userId, id);
            redirectAttributes.addFlashAttribute("successMessage", "レシピを削除しました");
        } catch (Exception e) {
            log.error("[RecipeDelete] 削除エラー: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "レシピの削除に失敗しました");
        }

        return "redirect:/recipes";
    }

    // レシピ詳細データ取得 (JSON - 編集モーダル等用)
    @GetMapping("/api/{id}")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> getRecipeApi(@PathVariable("id") Integer id,
                                                                   @AuthenticationPrincipal LoginUser loginUser) {
        Integer userId = loginUser.getId();
        Recipe recipe = recipeService.getRecipeDetail(id);
        if (recipe == null || !recipe.getUserId().equals(userId)) {
            return org.springframework.http.ResponseEntity.notFound().build();
        }
        return org.springframework.http.ResponseEntity.ok(recipe);
    }

    // レシピ提案依頼を受け取り、提案されたレシピをDBに保存し、レシピページに遷移
    @PostMapping
    public String getRecipe(@AuthenticationPrincipal LoginUser loginUser,
                            @RequestParam(value = "selectedIds", required = false) List<Integer> selectedIds,
                            @RequestParam(value = "genre", required = false) String genre,
                            @RequestParam(value = "prioritizeExpiring", defaultValue = "false") boolean prioritizeExpiring,
                            @RequestParam(value = "lowCalorie", defaultValue = "false") boolean lowCalorie,
                            @RequestParam(value = "easyMode", defaultValue = "false") boolean easyMode) {

        Integer userId = loginUser.getId();
        List<Items> sourceItems;
        boolean isSelectionMode = false;

        if (selectedIds != null && !selectedIds.isEmpty()) {
            sourceItems = itemQueryService.getSourceItemsById(userId, selectedIds);
            isSelectionMode = true;
        } else {
            sourceItems = itemQueryService.getSourceItems(userId);
        }

        String genreParam = (genre == null || genre.isEmpty()) ? "お任せ" : genre;

        RecipeResponse response = openAiService.getRecipeSuggestion(sourceItems, genreParam, prioritizeExpiring,
                lowCalorie, easyMode, isSelectionMode);

        recipeService.saveRecipe(userId, response, sourceItems);

        return "redirect:/recipes";
    }

    // 過去レシピ一覧
    @GetMapping("/history")
    public String getRecipeHistory(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        Integer userId = loginUser.getId();
        List<Recipe> history = recipeService.findByUserId(userId);
        model.addAttribute("history", history);
        return "recipe_history";
    }

    // 既存の特定レシピ表示用
    @GetMapping("/{id}")
    public String getRecipeDetail(@PathVariable("id") Integer id, Model model) {
        Recipe recipe = recipeService.getRecipeDetail(id);
        model.addAttribute("recipe", recipe);
        return "recipe";
    }
}
