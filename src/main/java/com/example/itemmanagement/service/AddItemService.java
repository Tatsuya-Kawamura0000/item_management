package com.example.itemmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.form.AddItemForm;
import com.example.itemmanagement.mapper.ItemMapper;

@Service
public class AddItemService {

    @Autowired
    private ItemMapper mapper;


    @Transactional
    public Items add(AddItemForm form, Integer userId) {

        Items entity = createEntityFromForm(form,userId);

        mapper.add(entity);

        return entity;
    }
    

    /**
     * 共通の変換処理
     */
    private Items createEntityFromForm(AddItemForm form, Integer userId) {
        Items entity = new Items();
        entity.setName(form.getName());
        entity.setCategoryId(form.getCategoryId());
        entity.setDeadline(form.getDeadline());
        entity.setPurchaseDate(form.getPurchaseDate());
        entity.setAmount(form.getAmount());
        entity.setOthers(form.getOthers());
        entity.setStatus(1);
        entity.setFavorite(form.isFavorite());
        entity.setUserId(userId);

        return entity;
    }

    @Transactional
    public void markAsPurchased(int id, Integer userId) {
        // Mapper に userId を渡して削除
        mapper.deleteFromShoppingList(id, userId);
    }
}
