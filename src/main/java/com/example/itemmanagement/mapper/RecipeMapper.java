package com.example.itemmanagement.mapper;

import com.example.itemmanagement.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecipeMapper {

    void insertRecipe(Recipe recipe);

    //保存済みレシピ表示
    List<Recipe> selectRecipe (@Param("userId") Integer userId);
}
