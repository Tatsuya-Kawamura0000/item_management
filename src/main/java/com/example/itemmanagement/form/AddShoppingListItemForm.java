package com.example.itemmanagement.form;

import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class AddShoppingListItemForm {
	
	@Size(max = 50, message = "食材は50文字以内で入力してください")
    private String name;   // 食材名
	
	@Size(max = 20, message = "量は20文字以内で入力してください")
    private String amount; // 量
}
