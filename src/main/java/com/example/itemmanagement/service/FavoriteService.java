package com.example.itemmanagement.service;

import com.example.itemmanagement.entity.Items;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {

    private final ItemQueryService itemQueryService;
    private final UpdateItemService updateItemService;

    public FavoriteService(ItemQueryService itemQueryService, UpdateItemService updateItemService) {

        this.itemQueryService = itemQueryService;
        this.updateItemService = updateItemService;
    }

    public void toggleFavorite(int itemId, Integer userId) {

        Items item = itemQueryService.getItemById(itemId, userId);

        item.setFavorite(!item.isFavorite());

        updateItemService.updateFavorite(item, userId);
    }
}
