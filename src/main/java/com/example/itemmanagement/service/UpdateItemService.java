package com.example.itemmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.mapper.ItemMapper;
import com.example.itemmanagement.mapper.ShoppingListMapper;

@Service
public class UpdateItemService {

    @Autowired
    private ItemMapper mapper;
    
    @Autowired
    private ShoppingListMapper shoppingListMapper;

    @Transactional
    public void updateItem(int id, Integer userId, Items item) {
        // 更新対象IDとユーザーIDをセット
        item.setId(id);
        item.setUserId(userId);

        mapper.update(item);
    }

    @Transactional
    public void updateFavorite(Items item, Integer userId) {
        item.setUserId(userId);
        mapper.updateFavorite(item);
    }
    
    @Transactional
    public void bulkDelete(List<Integer> ids, Integer userId) {

        for(Integer id : ids){
        	shoppingListMapper.stop(id, userId);
        }

    }

}