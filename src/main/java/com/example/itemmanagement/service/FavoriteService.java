package com.example.itemmanagement.service;

import com.example.itemmanagement.entity.Items;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {

    private final GetAllItemsService getAllItemsService;
    private final UpdateItemService updateItemService;

    public FavoriteService(
            GetAllItemsService getAllItemsService,
            UpdateItemService updateItemService) {

        this.getAllItemsService = getAllItemsService;
        this.updateItemService = updateItemService;
    }

    public void toggleFavorite(int itemId, Integer userId) {

        Items item = getAllItemsService.getItemById(itemId, userId);

        item.setFavorite(!item.isFavorite());

        updateItemService.updateFavorite(item, userId);
    }
}
