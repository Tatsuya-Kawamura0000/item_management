package com.example.itemmanagement.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShoppingListItem {

    private int id;                // 自動採番
    private Integer itemId;            // items.id
    private Integer userId;            // user.id
    private boolean status;            // 1 = 有効
    private LocalDateTime addedAt;     // リストに追加した日時
    private boolean purchasedFlg;      // 0=未購入 / 1=購入済

    private String name;               // 食材名
    private String amount;             // 量
    private Integer categoryId;        // カテゴリID
    private String categoryName;       // カテゴリ名
    private LocalDate purchaseDate;    // 購入日
}
