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
	public int add(AddItemForm form, Integer userId) {

	    Items entity = new Items();

	    entity.setName(form.getName());
	    entity.setCategoryId(form.getCategoryId());
	    entity.setDeadline(form.getDeadline());
	    entity.setPurchaseDate(form.getPurchaseDate());
	    entity.setAmount(form.getAmount());
	    entity.setOthers(form.getOthers());
	    entity.setStatus(1);                // 初期状態は「有効」
	    entity.setFavorite(form.isFavorite());
	    entity.setUserId(userId);           // ここでログインユーザーIDをセット

	    return mapper.add(entity);          // ItemMapperのaddメソッドを呼び出す
	}
	
	@Transactional
	public Items addAndReturn(AddItemForm form, Integer userId) {

	    Items entity = createEntityFromForm(form);

	    entity.setUserId(userId);

	    mapper.add(entity);

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
    public void deleteFromShoppingList(int id, Integer userId) {
        // Mapper に userId を渡して削除
        mapper.deleteFromShoppingList(id, userId);
    }
}
