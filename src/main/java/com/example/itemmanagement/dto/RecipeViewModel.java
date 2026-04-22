package com.example.itemmanagement.dto;
import com.example.itemmanagement.entity.Recipe;
import java.util.List;


public class RecipeViewModel {

    private List<Recipe> recipe;

    public RecipeViewModel(

            List<Recipe> recipe){

        this.recipe = recipe;
    }

    // getter
    public List<Recipe> getRecipe() { return recipe; }

}