package com.example.itemmanagement.controller;

import com.example.itemmanagement.dto.DashboardViewModel;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.ItemSummaryService;
import com.example.itemmanagement.service.RecipeHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private ItemSummaryService itemSummaryService;

    @Autowired
    private RecipeHomeService recipeHomeService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal LoginUser loginUser) {

        DashboardViewModel dashboardData = itemSummaryService.getDashboardData(loginUser.getId());

        model.addAttribute("summary", dashboardData.getSummary());
        model.addAttribute("soonFoods", dashboardData.getSoonFoods());
        model.addAttribute("expiredFoods", dashboardData.getExpiredFoods());
        model.addAttribute("rvm", recipeHomeService.getRecipeHomeData(loginUser.getId()));
        List<com.example.itemmanagement.entity.Recipe> menuRecipes = recipeHomeService.getRecipesAvailable(loginUser.getId());
        Map<Integer, Long> menuCategoryCountMap = menuRecipes.stream()
                .filter(recipe -> recipe.getCategoryId() != null)
                .collect(Collectors.groupingBy(com.example.itemmanagement.entity.Recipe::getCategoryId, Collectors.counting()));
        model.addAttribute("menuRecipes", menuRecipes);
        model.addAttribute("menuCategoryCountMap", menuCategoryCountMap);

        return "dashboard";
    }

}
