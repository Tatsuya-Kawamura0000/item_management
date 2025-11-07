package com.example.itemmanagement.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.itemmanagement.entity.ShoppingListItem;

@Mapper
public interface ShoppingListMapper {

	int insert(ShoppingListItem slItem); // items テーブルから取得した情報を shopping_list にINSERT
	
	List<ShoppingListItem> findAll();  // 一覧取得用
	
}
