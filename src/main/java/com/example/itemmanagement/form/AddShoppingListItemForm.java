package com.example.itemmanagement.form;

import lombok.Data;

@Data
public class AddShoppingListItemForm {
    private String name;   // 食材名
    private String amount; // 量
}
