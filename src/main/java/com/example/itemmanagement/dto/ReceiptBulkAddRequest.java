package com.example.itemmanagement.dto;

import com.example.itemmanagement.form.AddItemForm;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ReceiptBulkAddRequest {
    @NotEmpty(message = "登録する食材がありません")
    private List<@Valid AddItemForm> items;
}
