package com.example.itemmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.mapper.CategoryMapper;

@Service
public class GetAllCategoriesService {					//全カテゴリーをリスト型で返却するサービス
	
	@Autowired
    private CategoryMapper mapper;

    @Transactional
    public List<Categories> getAllCategories(){

        return mapper.findAll();

    }

}
