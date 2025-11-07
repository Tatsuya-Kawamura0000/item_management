package com.example.itemmanagement.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShoppingListItem {
	
    private int id;                // 🆕 shopping_list.id（自動採番）
    private int itemId;            // items.id
    private int userId;            // 現状は仮で0
    private boolean status;        // true=有効
    private LocalDateTime addedAt; // 追加日時
    private boolean purchasedFlg;  // false=未購入

    // 将来的に一覧表示やチェック機能に必要なフィールド
    private String name;           // 食材名
    private String amount;         // 量
    private Integer categoryId;    // カテゴリID
    private String categoryName;   // カテゴリ名（JOINで取得）
    private LocalDate purchaseDate; // 購入日

}
