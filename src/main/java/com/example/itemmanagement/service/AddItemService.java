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

		return mapper.add(entity); //UserMapperのcreateメソッド
		
	}

}
