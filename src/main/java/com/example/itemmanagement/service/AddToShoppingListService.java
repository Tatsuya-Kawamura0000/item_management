package com.example.itemmanagement.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private ItemMapper itemMapper;

    @Autowired
    private ShoppingListMapper shoppingListMapper;

    public List<String> addItemToList(int id, Integer userId) {

        //重複しているアイテムリスト
        List<String> duplicatedNames = new ArrayList<>();

        Items item = itemMapper.findById(id, userId);

        if (item == null) {
            duplicatedNames.add("存在しない食材");
            return duplicatedNames;
        }

        //重複チェックのサービスを呼び出し
        boolean exists = shoppingListMapper.existsByName(item.getName(), userId);

        //重複していた場合
        if (exists) {
            duplicatedNames.add(item.getName());
            return duplicatedNames;
        }

        //重複がなかったので、買い物リストに追加
        ShoppingListItem slItem = new ShoppingListItem();
        slItem.setItemId(item.getId());
        slItem.setUserId(userId);
        slItem.setStatus(true);
        slItem.setAddedAt(LocalDateTime.now());
        slItem.setPurchasedFlg(false);
        slItem.setName(item.getName());
        slItem.setAmount(item.getAmount());
        slItem.setCategoryId(item.getCategoryId());
        slItem.setCategoryName(item.getCategoryName());
        slItem.setPurchaseDate(item.getPurchaseDate());

        shoppingListMapper.insert(slItem);

        return duplicatedNames; // 空＝成功;
    }
    
    public ShoppingListItem addNewItem(AddShoppingListItemForm form, Integer userId) {

        ShoppingListItem item = new ShoppingListItem();
        item.setItemId(null);
        item.setUserId(userId);   // ← ログインユーザー
        item.setName(form.getName());
        item.setAmount(form.getAmount());
        item.setStatus(true);
        item.setPurchasedFlg(false);
        item.setAddedAt(LocalDateTime.now());

        shoppingListMapper.insert(item);

        return item;
    }
    
    public int getShoppingListCount(Integer userId) {
    	
        return shoppingListMapper.countByUserId(userId);
        
    }

}
