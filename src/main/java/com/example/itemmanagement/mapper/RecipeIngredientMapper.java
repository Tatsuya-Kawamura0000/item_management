package com.example.itemmanagement.mapper;

import com.example.itemmanagement.entity.RecipeIngredient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecipeIngredientMapper {

    void batchInsert(@Param("ingredients") List<RecipeIngredient> ingredients);

    List<RecipeIngredient> selectByRecipeId(@Param("recipeId") Integer recipeId);

    void deleteByRecipeId(@Param("recipeId") Integer recipeId);

}
