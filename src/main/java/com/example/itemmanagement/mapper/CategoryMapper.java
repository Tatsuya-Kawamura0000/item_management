package com.example.itemmanagement.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.itemmanagement.entity.Categories;

@Mapper	
public interface CategoryMapper {
	
	List<Categories> findAll();

}
