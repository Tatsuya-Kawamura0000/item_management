package com.example.itemmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.mapper.ItemMapper;

@Service
public class GetFilterItemsService {

    @Autowired
    private ItemMapper itemMapper;  // Mapper を注入

    /**
     * ユーザーID + 条件でアイテムをフィルター
     */
    public List<Items> filterItems(Integer category, Boolean expiringSoon,Boolean expired, Integer userId) {
        return itemMapper.filterItems(userId, category, expiringSoon,expired);
    }


}
