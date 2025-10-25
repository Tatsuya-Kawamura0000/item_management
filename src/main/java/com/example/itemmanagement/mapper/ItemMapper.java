package com.example.itemmanagement.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.itemmanagement.entity.Items;

@Mapper						
public interface ItemMapper {
	
	List<Items> findAll();  //items一覧を返すメソッド

	int add(Items entity);
}
