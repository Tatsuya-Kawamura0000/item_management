package com.example.itemmanagement.entity;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data						//クラスに対してゲッターやセッター等の便利メソッドを自動的に生成
@NoArgsConstructor			//デフォルトコンストラクタを自動的に生成
@AllArgsConstructor			//クラスのすべてのフィールドを引数として持つコンストラクタを自動的に生成
public class Items {

	private int id;

	private String name;

	private Integer categoryId;

	private String amount;

	private LocalDate deadline;

	private LocalDate purchaseDate;

	private String others;

	private Integer status;
	
	private String categoryName;
	
	private boolean expiringSoon;		//期限が3日以内であれば、true
	
	private boolean favorite;
	
}
