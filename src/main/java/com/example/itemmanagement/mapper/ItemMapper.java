package com.example.itemmanagement.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.itemmanagement.entity.Items;

@Mapper						
public interface ItemMapper {
	
	List<Items> findAll();  //items一覧を返すメソッド

	int add(Items entity);
	
	int stop(int id); //statsを0に変更（消費済み）するメソッド
	
	Items findById(int id);
	
	int update (Items item);
	
	int updateFavorite(Items item);

    // 複数条件でフィルター
    List<Items> filterItems(@Param("category") Integer category,
                           @Param("expiringSoon") Boolean expiringSoon);
	
}
