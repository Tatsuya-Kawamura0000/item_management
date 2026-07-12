package com.example.itemmanagement.service;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.mapper.ItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetFilterItemsService {

    @Autowired
    private ItemMapper itemMapper;  // Mapper を注入

    /**
     * ユーザーID + 条件でアイテムをフィルター
     */
    public List<Items> filterItems(Integer category, Boolean expiringSoon, Boolean expired, Integer userId) {
        return itemMapper.filterItems(userId, category, expiringSoon, expired);
    }


}
