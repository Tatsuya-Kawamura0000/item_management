package com.example.itemmanagement.dto;

import lombok.Data;

/** AIがレシートから抽出した、登録前の1食材。 */
@Data
public class ReceiptItemDto {
    private String name;
    private Integer quantity;
    private String unit;
    private String category;
    private Integer categoryId;
    private String expirationDate;
}
