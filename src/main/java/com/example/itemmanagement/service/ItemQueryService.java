package com.example.itemmanagement.service;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.mapper.ItemMapper;
import com.example.itemmanagement.mapper.ItemSearchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemQueryService {

    @Autowired
    private ItemMapper mapper;

    @Autowired
    private ItemSearchMapper itemSearchmapper;

    @Transactional
    public List<Items> getAllItems(Integer userId) {

        return mapper.findAll(userId); //mapper.findAll()メソッドで取得したitems一覧をItems型のリストで返却

    }

    @Transactional
    public Items getItemById(int id, Integer userId) {

        return mapper.findById(id, userId);
    }

    @Transactional
    public List<Items> getSourceItems(Integer userId) {

        return mapper.getSourceItems(userId);  //レシピ作成で渡すための食材リストを返す

    }

    @Transactional
    public List<Items> getSourceItemsById(Integer userId, List<Integer> selectedIds) {

        return mapper.getSourceItemsById(userId, selectedIds);  //レシピ作成で渡すための食材リストを返す

    }

    @Transactional
    public List<Items> filterItems(Integer category, Boolean expiringSoon, Boolean expired, Integer userId) {
        return mapper.filterItems(userId, category, expiringSoon, expired);
    }

    @Transactional
    public List<Items> search(String searchType, String keyword, Integer userId) {

        return itemSearchmapper.searchItems(searchType, keyword, userId);

    }
}

