package com.example.itemmanagement.service;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.mapper.ItemSearchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemSearchService {

    private final ItemSearchMapper itemSearchmapper;

    @Transactional
    public List<Items> search(String searchType, String keyword, Integer userId) {

        return itemSearchmapper.searchItems(searchType, keyword, userId);

    }
}
