package com.example.itemmanagement.form;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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

	private LocalDate deadline;

	private LocalDate purchaseDate;

	@Size(max = 100, message = "その他は100文字以内で入力してください")
	private String others;

	private Integer status;
	
	private String categoryName;
	
	private boolean favorite;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public LocalDate getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDate deadline) {
		this.deadline = deadline;
	}

	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(LocalDate purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getOthers() {
		return others;
	}

	public void setOthers(String others) {
		this.others = others;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	

}
