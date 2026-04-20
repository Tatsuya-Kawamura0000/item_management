package com.example.itemmanagement.form;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Data						//クラスに対してゲッターやセッター等の便利メソッドを自動的に生成
@NoArgsConstructor			//デフォルトコンストラクタを自動的に生成
@AllArgsConstructor	
public class AddItemForm {
	
	private int id;

	@Size(max = 50, message = "食材は50文字以内で入力してください")
	private String name;

	private Integer categoryId;

	@Size(max = 20, message = "量は20文字以内で入力してください")
	private String amount;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate deadline;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate purchaseDate;

	@Size(max = 100, message = "その他は100文字以内で入力してください")
	private String others;

	private Integer status;
	
	private String categoryName;
	
	private boolean favorite;

}
