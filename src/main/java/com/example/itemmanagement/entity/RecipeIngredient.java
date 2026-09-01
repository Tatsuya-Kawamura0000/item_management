package com.example.itemmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredient {

    private Integer id;
    private Integer recipeId;
    private String ingredientName;
    private BigDecimal quantity;
    private String unit;
    private Boolean isMain;
    private Integer displayOrder;
    private Integer categoryId; // 食材カテゴリID (categories.id)

}
