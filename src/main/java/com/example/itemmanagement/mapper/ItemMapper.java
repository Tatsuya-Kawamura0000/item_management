package com.example.itemmanagement.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.itemmanagement.entity.Items;

@Mapper						
public interface ItemMapper {
	
	List<Items> findAll(@Param("userId") Integer userId);  //items一覧を返すメソッド

	int add(Items entity);
	
	int stop(@Param("id") Integer id,@Param("userId") Integer userId); //statsを0に変更（消費済み）するメソッド
	         
	
	Items findById(@Param("id") Integer id,@Param("userId") Integer userId);
		    
	
	int update (Items item);
	
	int updateFavorite(Items item);

    // 複数条件でフィルター
    List<Items> filterItems(@Param("userId") Integer userId,
    	    @Param("category") Integer category,
    	    @Param("expiringSoon") Boolean expiringSoon);
    
    // shopping_list から削除
    int deleteFromShoppingList(@Param("id") Integer id,@Param("userId") Integer userId);
    
    //mail送信用
    List<Items> findExpiringItems();
    	    
	
}
