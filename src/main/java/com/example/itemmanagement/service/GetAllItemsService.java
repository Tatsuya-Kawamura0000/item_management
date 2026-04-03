package com.example.itemmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.mapper.ItemMapper;

@Service
public class GetAllItemsService {
	
	@Autowired 
	private ItemMapper mapper;

	@Transactional 
	public List<Items> getAllItems(Integer userId) {

		return mapper.findAll(userId); //mapper.findAll()メソッドで取得したitems一覧をItems型のリストで返却

	}
	
	public Items getItemById(int id, Integer userId) {
		
        return mapper.findById(id, userId);
    }

}
