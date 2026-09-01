package com.example.itemmanagement.mapper;

import com.example.itemmanagement.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecipeMapper {

    void insertRecipe(Recipe recipe);

    Recipe findById(@Param("id") Integer id);

    // 保存済み最新レシピ表示
    List<Recipe> selectRecipe(@Param("userId") Integer userId);

    // ユーザーの全レシピ取得
    List<Recipe> selectByUserId(@Param("userId") Integer userId);

    // レシピ更新
    int updateRecipe(Recipe recipe);

    // レシピ削除
    int deleteByIdAndUserId(@Param("id") Integer id, @Param("userId") Integer userId);
}
