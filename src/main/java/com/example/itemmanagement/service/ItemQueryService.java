package com.example.itemmanagement.service;
import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.mapper.ItemMapper;
import com.example.itemmanagement.mapper.ItemSearchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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

    /**
     * 直近3回分の購入日ごとにアイテムを取得してグルーピングした DTO リストを返す
     */
    @Transactional
    public List<com.example.itemmanagement.dto.RecentPurchaseGroupDto> getRecentPurchaseGroups(Integer userId) {

        List<Items> items = mapper.getItemsByRecentPurchaseDates(userId);

        // Group by purchaseDate
        Map<LocalDate, List<com.example.itemmanagement.dto.RecentPurchaseItemDto>> grouped = new LinkedHashMap<>();

        for (Items it : items) {
            LocalDate pd = it.getPurchaseDate();
            if (pd == null) continue;
            grouped.computeIfAbsent(pd, k -> new ArrayList<>())
                    .add(new com.example.itemmanagement.dto.RecentPurchaseItemDto() {{
                        setId(it.getId());
                        setName(it.getName());
                        setPurchaseAmount(it.getPurchaseAmount());
                        setCategoryId(it.getCategoryId());
                    }});
        }

        List<com.example.itemmanagement.dto.RecentPurchaseGroupDto> result = new ArrayList<>();
        for (Map.Entry<LocalDate, List<com.example.itemmanagement.dto.RecentPurchaseItemDto>> e : grouped.entrySet()) {
            com.example.itemmanagement.dto.RecentPurchaseGroupDto g = new com.example.itemmanagement.dto.RecentPurchaseGroupDto();
            g.setPurchaseDate(e.getKey());
            g.setItems(e.getValue());
            result.add(g);
        }

        return result;
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

    @Transactional
    public List<Items> getFavoriteItems(Integer userId) {

        return mapper.getFavoriteItems(userId);

    }

}

