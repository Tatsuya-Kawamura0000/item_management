package com.example.itemmanagement.mapper;

import com.example.itemmanagement.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecipeMapper {

    void insertRecipe(Recipe recipe);
}
