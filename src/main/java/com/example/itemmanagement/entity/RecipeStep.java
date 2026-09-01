package com.example.itemmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStep {

    private Integer id;
    private Integer recipeId;
    private Integer stepNo;
    private String instruction;

}
