package com.example.itemmanagement.dto;

public class ItemSummary {

    private int expiredCount;  //期限切れ食材数
    private int warningCount;  //期限間近食材数

    public ItemSummary(int expiredCount, int warningCount) {
        this.expiredCount = expiredCount;
        this.warningCount = warningCount;
    }

    // getterも忘れずに
    public int getExpiredCount() {
        return expiredCount;
    }

    public int getWarningCount() {
        return warningCount;
    }
}

