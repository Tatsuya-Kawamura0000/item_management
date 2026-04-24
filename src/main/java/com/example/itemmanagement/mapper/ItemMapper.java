package com.example.itemmanagement.mapper;

import com.example.itemmanagement.entity.Items;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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
    	    @Param("expiringSoon") Boolean expiringSoon,
			@Param("expired") Boolean expired); // ←期限切れアラートリンク用に追加);
    
    // shopping_list から削除
    int deleteFromShoppingList(@Param("id") Integer id,@Param("userId") Integer userId);
    
    //mail送信用
    List<Items> findExpiringItems();

	//カテゴリーごとのアイテム数をカウント
	List<Map<String, Object>> countItemsByCategory(@Param("userId") Integer userId);

	//AIに投げる用のアイテム取得
	List<Items> getSourceItems(@Param("userId") Integer userId);

	//選択されたアイテムIDでアイテムを取得する
	List<Items> getSourceItemsById(@Param("userId") Integer userId, @Param("selectedIds") List<Integer> selectedIds);

	//アイテム検索
	List<Items> searchItems(@Param("userId") Integer userId);

}
