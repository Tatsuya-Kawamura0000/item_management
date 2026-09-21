package com.example.itemmanagement.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReceiptReadResponse {
    private List<ReceiptItemDto> items = new ArrayList<>();
}
