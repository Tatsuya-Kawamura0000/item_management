package com.example.itemmanagement.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.mapper.ItemMapper;

@Service
public class UpdateItemService {
	
	    @Autowired
	    private ItemMapper mapper;

	    public void updateItem(int id, Items item) {
	    	
	        item.setId(id);
	        
	        mapper.update(item);
	        
	    }
	    
	    public void updateFavorite(Items item) {
	    	
	        mapper.updateFavorite(item);
	    }

	    
	}

