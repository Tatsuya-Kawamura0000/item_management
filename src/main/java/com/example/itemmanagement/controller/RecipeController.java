package com.example.itemmanagement.controller;

import com.example.itemmanagement.dto.RecipeResponse;
import com.example.itemmanagement.dto.RecipeViewModel;
import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.entity.Recipe;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.GetAllItemsService;
import com.example.itemmanagement.service.OpenAiService;
import com.example.itemmanagement.service.RecipeHomeService;
import com.example.itemmanagement.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {

    private final OpenAiService openAiService;
    private final RecipeService recipeService;
    private final GetAllItemsService getAllItemsService;
    private final RecipeHomeService recipeHomeService;

    //レシピ画面を返す
    @GetMapping
    public String Recipe(Model model,
                         @AuthenticationPrincipal LoginUser loginUser) {

        // ログインユーザーID情報格納
        Integer userId = loginUser.getId();

        //モデルに埋め込み
        RecipeViewModel rvm = recipeHomeService.getRecipeHomeData(userId);

        model.addAttribute("rvm", rvm);

        return "recipe";

    }

    //レシピ提案依頼を受け取り、提案されたレシピをDBに保存し、レシピページに遷移
    //引数のgenreは、作成してもらうレシピのジャンル指定
    @PostMapping
    public String getRecipe(@AuthenticationPrincipal LoginUser loginUser ,
                            @RequestParam(value = "selectedIds", required = false) List<Integer> selectedIds,
                            @RequestParam(value = "genre", required = false) String genre,
                            @RequestParam(value = "prioritizeExpiring", defaultValue = "false") boolean prioritizeExpiring,
                            @RequestParam(value = "lowCalorie", defaultValue = "false") boolean lowCalorie,
                            @RequestParam(value = "easyMode", defaultValue = "false") boolean easyMode) {



        // ログインユーザーID情報格納
        Integer userId = loginUser.getId();

        List<Items> sourceItems;

        //selectedIdsが飛んで来たらtrue にする。
        boolean isSelectionMode = false; // フラグを用意


        if (selectedIds != null && !selectedIds.isEmpty()) {
            // パターンA: 選択したアイテムを使用
            sourceItems = getAllItemsService.getSourceItemsById(userId,selectedIds);  //作成必要
            isSelectionMode = true; // 　選択されている場合は true
        } else {
            // パターンB: 全件を使用
            sourceItems = getAllItemsService.getSourceItems(userId);  //既存
        }

        // ジャンルが空の場合は 「お任せ」として扱う
        String genreParam = (genre == null || genre.isEmpty()) ? "お任せ" : genre;

        RecipeResponse response = openAiService.getRecipeSuggestion(sourceItems, genreParam, prioritizeExpiring,
                lowCalorie, easyMode,isSelectionMode);

        //返ってきた提案レシピをDBに保存
        recipeService.saveRecipe(userId, response, sourceItems);

        return "redirect:/recipes";

    }

    //過去レシピ一覧　押下時
    @GetMapping("/history")
    public String getRecipeHistory(Model model, @AuthenticationPrincipal LoginUser loginUser) {

        // ログインユーザーID情報格納
        Integer userId = loginUser.getId();

        // ログインユーザーの過去レシピを直近30件取得
        List<Recipe> history = recipeService.findByUserId(userId);

        model.addAttribute("history", history);

        return "recipe_history";
    }

    // 既存の特定レシピ表示用（履歴からクリックした時用）
    @GetMapping("/{id}")
    public String getRecipeDetail(@PathVariable("id") Integer id, Model model) {
        // IDから1件取得してRecipeViewModel(rvm)に詰めて返す処理
        // ...
        return "recipe";
    }


}



