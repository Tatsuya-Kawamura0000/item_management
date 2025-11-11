package com.example.itemmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.itemmanagement.mapper.ItemMapper; 

import com.example.itemmanagement.entity.Items;

@Service
public class GetFilterItemsService {

    @Autowired
    private ItemMapper itemMapper;  // Mapper を注入

    public List<Items> filterItems(Integer category, Boolean expiringSoon) {
        return itemMapper.filterItems(category, expiringSoon);
    }

}
