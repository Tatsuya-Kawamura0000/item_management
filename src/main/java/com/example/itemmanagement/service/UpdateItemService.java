package com.example.itemmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.mapper.ItemMapper;

@Service
public class UpdateItemService {

    @Autowired
    private ItemMapper mapper;

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

}