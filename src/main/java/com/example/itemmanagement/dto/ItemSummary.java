package com.example.itemmanagement.dto;

public class ItemSummary {

    private int expiredCount;  // 期限切れ食材数
    private int warningCount;  // 期限間近食材数
    private int totalCount;    // 全食材数（追加）

    public ItemSummary(int expiredCount, int warningCount, int totalCount) {
        this.expiredCount = expiredCount;
        this.warningCount = warningCount;
        this.totalCount = totalCount;
    }

    public int getExpiredCount() {
        return expiredCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public int getTotalCount() {
        return totalCount;
    }
}