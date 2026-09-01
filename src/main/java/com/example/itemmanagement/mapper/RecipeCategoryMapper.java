package com.example.itemmanagement.mapper;

import com.example.itemmanagement.entity.RecipeCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RecipeCategoryMapper {

    List<RecipeCategory> findAll();

}
