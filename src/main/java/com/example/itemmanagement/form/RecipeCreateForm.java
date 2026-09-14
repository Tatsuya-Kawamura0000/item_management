package com.example.itemmanagement.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreateForm {

    private String recipeName;

    private Integer servings = 2; // デフォルト2人分

    private Integer categoryId; // レシピカテゴリID

    private String sourceType = "USER"; // デフォルト: USER

    private String sourceUrl;

    private String memo;

    @Valid
    private List<IngredientForm> ingredients = new ArrayList<>();

    @Valid
    private List<StepForm> steps = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientForm {
        private String ingredientName;
        private BigDecimal quantity;
        private String unit;
        private Boolean isMain = false;
        private Integer categoryId; // 食材カテゴリID
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StepForm {
        private String instruction;
    }
}
