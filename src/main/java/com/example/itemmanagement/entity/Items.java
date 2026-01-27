package com.example.itemmanagement.entity;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data						//クラスに対してゲッターやセッター等の便利メソッドを自動的に生成
@NoArgsConstructor			//デフォルトコンストラクタを自動的に生成
@AllArgsConstructor			//クラスのすべてのフィールドを引数として持つコンストラクタを自動的に生成
public class Items {

	private int id;

	 @Size(max = 50, message = "食材は50文字以内で入力してください")
	private String name;

	private Integer categoryId;

	@Size(max = 20, message = "量は20文字以内で入力してください")
	private String amount;

	private LocalDate deadline;

	private LocalDate purchaseDate;

	 @Size(max = 100, message = "その他は100文字以内で入力してください")
	private String others;

	private Integer status;
	
	private String categoryName;
	
	private boolean expiringSoon;		//期限が3日以内であれば、true
	
	private boolean favorite;
	
	private String message; // 期限切れや注意メッセージ用
	
}
