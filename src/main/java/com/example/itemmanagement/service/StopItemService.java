package com.example.itemmanagement.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.itemmanagement.mapper.ItemMapper;

@Service
public class StopItemService {
	
	@Autowired 
	private ItemMapper mapper;

	@Transactional 
	public void stopItem(int id) {

		 mapper.stop(id); 			//mapper.stop(id)メソッドで,引数に渡したidを持つ食品のstatusを0に更新

	}

}
