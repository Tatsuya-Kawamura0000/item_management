package com.example.itemmanagement.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.itemmanagement.mapper.ItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.mapper.CategoryMapper;

@Service
public class GetAllCategoriesService {					//全カテゴリーをリスト型で返却するサービス
	
	@Autowired
    private CategoryMapper mapper;

    @Autowired
    private ItemMapper itemMapper;

    @Transactional
    public List<Categories> getAllCategories(){
        return mapper.findAll();

    }

    @Transactional
    public Map<Integer, Integer> getCategoryCounts(Integer userId){

        List<Map<String,Object>> results =
                itemMapper.countItemsByCategory(userId);

        Map<Integer,Integer> categoryCounts = new HashMap<>();

        for(Map<String,Object> row : results){

            Integer categoryId = (Integer) row.get("category_id");
            Long count = (Long) row.get("count");

            categoryCounts.put(categoryId, count.intValue());
        }

        return categoryCounts;
    }
}
