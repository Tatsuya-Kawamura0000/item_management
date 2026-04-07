package com.example.itemmanagement.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.itemmanagement.entity.ShoppingListItem;

@Mapper
public interface ShoppingListMapper {

    int insert(ShoppingListItem slItem); // items テーブルから取得した情報を shopping_list にINSERT

    // ユーザー単位で買い物リストを取得
    List<ShoppingListItem> findAll(@Param("userId") Integer userId);
    
    //// ユーザー単位で買い物リストのアイテム数を取得
    int countByUserId(Integer userId);
    
    int stop(@Param("id") Integer id,@Param("userId") Integer userId);

}
