package com.example.itemmanagement.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.entity.ShoppingListItem;
import com.example.itemmanagement.form.AddShoppingListItemForm;
import com.example.itemmanagement.mapper.ItemMapper;
import com.example.itemmanagement.mapper.ShoppingListMapper;

@Service
public class AddToShoppingListService {

    @Autowired
    private ItemMapper itemMapper;  // 食品を取得するため

    @Autowired
    private ShoppingListMapper shoppingListMapper; // 買い物リストへ追加するため

    public void addItemToList(int itemId) {
        // ① 食品情報を取得
        Items item = itemMapper.findById(itemId);

        if (item != null) {
        	ShoppingListItem slItem = new ShoppingListItem();
            slItem.setItemId(item.getId());                 // items.id → shopping_list.item_id
            slItem.setUserId(0);                            // 仮ユーザ
            slItem.setStatus(true);                          // 有効フラグ
            slItem.setAddedAt(LocalDateTime.now());         // 追加日時
            slItem.setPurchasedFlg(false);                  // 未購入フラグ
            slItem.setName(item.getName());                 // 食材名コピー
            slItem.setAmount(item.getAmount());             // 量コピー
            slItem.setCategoryId(item.getCategoryId());     // カテゴリIDコピー
            slItem.setCategoryName(item.getCategoryName()); // カテゴリ名コピー
            slItem.setPurchaseDate(item.getPurchaseDate()); // 購入日コピー
        	
            // ② ShoppingListテーブルにINSERT
            shoppingListMapper.insert(slItem);
        }
    }
    
    public ShoppingListItem addNewItem(AddShoppingListItemForm form) {
        ShoppingListItem item = new ShoppingListItem();
        item.setItemId(null);  // ← NEW（items に存在しないので null）
        item.setUserId(0);
        item.setName(form.getName());
        item.setAmount(form.getAmount());
        item.setStatus(true);
        item.setPurchasedFlg(false);
        item.setAddedAt(LocalDateTime.now());

        shoppingListMapper.insert(item);
        return item;
    }

}
