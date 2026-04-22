package com.example.itemmanagement.controller;

import com.example.itemmanagement.dto.RecipeViewModel;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.GetAllItemsService;
import com.example.itemmanagement.service.OpenAiService;
import com.example.itemmanagement.service.RecipeHomeService;
import com.example.itemmanagement.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


//@RestController
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



    /*
     //レシピ提案依頼を受け取り、提案されたレシピを返す
    @PostMapping
    public RecipeResponse getRecipe(@AuthenticationPrincipal LoginUser loginUser) {


        // ログインユーザーID情報格納
        Integer userId = loginUser.getId();


        //名前のみ取得し、格納
        List<String> sourceItems = getAllItemsService.getSourceItems(userId);

        //上記で取得したsourceItemsを渡し、OpenAIからの提案を受け取る
        RecipeResponse response = openAiService.getRecipeSuggestion(sourceItems);

        //返ってきた提案レシピをDBに保存
        recipeService.saveRecipe(userId, response, sourceItems);

        //DBに保存されたレシピを取得
        List<Recipe> suggestedRecipe = recipeService.getSuggestedRecipe(userId);


        //モデルに埋め込み
        RecipeViewModel rvm = recipehomeService.getRecipeHomeData(userId, suggestedRecipe);

        model.addAttribute("rvm", rvm);


        return "recipe";

    }*/


}



