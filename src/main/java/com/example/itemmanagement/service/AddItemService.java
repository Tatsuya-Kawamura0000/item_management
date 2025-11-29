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
	public int add(AddItemForm form) {

		Items entity = new Items();
		
		entity.setName(form.getName());

		entity.setCategoryId(form.getCategoryId());

		entity.setDeadline(form.getDeadline());

		entity.setPurchaseDate(form.getPurchaseDate());

		entity.setAmount(form.getAmount());
		
		entity.setOthers(form.getOthers());

		entity.setStatus(1);
		
		entity.setFavorite(form.isFavorite());

		return mapper.add(entity); //UserMapperのcreateメソッド
		
	}
	
	@Transactional
    public Items addAndReturn(AddItemForm form) {
        Items entity = createEntityFromForm(form);

        // DBに insert
        mapper.add(entity);

        // MyBatis の設定で auto-generated keys が有効なら entity に ID がセットされます
        return entity;
    }

    /**
     * 共通の変換処理
     */
    private Items createEntityFromForm(AddItemForm form) {
        Items entity = new Items();
        entity.setName(form.getName());
        entity.setCategoryId(form.getCategoryId());
        entity.setDeadline(form.getDeadline());
        entity.setPurchaseDate(form.getPurchaseDate());
        entity.setAmount(form.getAmount());
        entity.setOthers(form.getOthers());
        entity.setStatus(1);
        entity.setFavorite(form.isFavorite());
        return entity;
    }
    
    @Transactional
    public void deleteFromShoppingList(int id) {
        mapper.deleteFromShoppingList(id);
    }
}
