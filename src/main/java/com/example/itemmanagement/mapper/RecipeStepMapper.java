package com.example.itemmanagement.mapper;

import com.example.itemmanagement.entity.RecipeStep;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecipeStepMapper {

    void batchInsert(@Param("steps") List<RecipeStep> steps);

    List<RecipeStep> selectByRecipeId(@Param("recipeId") Integer recipeId);

    void deleteByRecipeId(@Param("recipeId") Integer recipeId);

}
