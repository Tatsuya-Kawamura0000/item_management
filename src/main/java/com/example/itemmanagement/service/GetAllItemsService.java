package com.example.itemmanagement.service;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.mapper.ItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetAllItemsService {
	
	@Autowired 
	private ItemMapper mapper;

	@Transactional 
	public List<Items> getAllItems(Integer userId) {

		return mapper.findAll(userId); //mapper.findAll()メソッドで取得したitems一覧をItems型のリストで返却

	}
	@Transactional
	public Items getItemById(int id, Integer userId) {
		
        return mapper.findById(id, userId);
    }

	@Transactional
	public List<String> getSourceItems(Integer userId) {

		return mapper.getSourceItems(userId);

	}
}
