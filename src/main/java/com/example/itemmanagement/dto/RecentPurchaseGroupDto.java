package com.example.itemmanagement.dto;

import java.time.LocalDate;
import java.util.List;

public class RecentPurchaseGroupDto {
    private LocalDate purchaseDate;
    private List<RecentPurchaseItemDto> items;

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public List<RecentPurchaseItemDto> getItems() {
        return items;
    }

    public void setItems(List<RecentPurchaseItemDto> items) {
        this.items = items;
    }
}
