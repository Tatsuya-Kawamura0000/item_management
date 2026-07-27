package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.ItemSummary;
import com.example.itemmanagement.entity.Items;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemSummaryServiceTest {

    private final ItemSummaryService sut = new ItemSummaryService();

    @Test
    void 期限切れの食材を1件カウントできること() {

        // Arrange
        Items item = new Items();
        item.setDeadline(LocalDate.now().minusDays(1));

        List<Items> items = List.of(item);

        // Act
        ItemSummary result = sut.summarize(items);

        // Assert
        assertEquals(1, result.getExpiredCount());
        assertEquals(0, result.getWarningCount());
    }


    @Test
    void 期限間近の食材を1件カウントできること() {

        // Arrange
        Items item = new Items();
        item.setDeadline(LocalDate.now().plusDays(3));

        List<Items> items = List.of(item);

        // Act
        ItemSummary result = sut.summarize(items);

        // Assert
        assertEquals(0, result.getExpiredCount());
        assertEquals(1, result.getWarningCount());
    }


    @Test
    void 本日が期限の食材を期限間近として1件カウントできること() {

        // Arrange
        Items item = new Items();
        item.setDeadline(LocalDate.now());

        List<Items> items = List.of(item);

        // Act
        ItemSummary result = sut.summarize(items);

        // Assert
        assertEquals(0, result.getExpiredCount());
        assertEquals(1, result.getWarningCount());
    }


    @Test
    void 期限が4日以上先の食材はカウントされないこと() {

        // Arrange
        Items item = new Items();
        item.setDeadline(LocalDate.now().plusDays(4));

        List<Items> items = List.of(item);

        // Act
        ItemSummary result = sut.summarize(items);

        // Assert
        assertEquals(0, result.getExpiredCount());
        assertEquals(0, result.getWarningCount());
    }


    @Test
    void 期限日がnullの食材はカウントされないこと() {

        // Arrange
        Items item = new Items();

        List<Items> items = List.of(item);

        // Act
        ItemSummary result = sut.summarize(items);

        // Assert
        assertEquals(0, result.getExpiredCount());
        assertEquals(0, result.getWarningCount());
    }


    @Test
    void 複数の食材を正しく集計できること() {

        // Arrange
        Items expiredItem = new Items();
        expiredItem.setDeadline(LocalDate.now().minusDays(1));

        Items warningItem = new Items();
        warningItem.setDeadline(LocalDate.now().plusDays(2));

        Items normalItem = new Items();
        normalItem.setDeadline(LocalDate.now().plusDays(10));

        List<Items> items =
                List.of(expiredItem, warningItem, normalItem);

        // Act
        ItemSummary result = sut.summarize(items);

        // Assert
        assertEquals(1, result.getExpiredCount());
        assertEquals(1, result.getWarningCount());
    }


    @Test
    void 空のリストの場合は0件を返却すること() {

        // Arrange
        List<Items> items = List.of();

        // Act
        ItemSummary result = sut.summarize(items);

        // Assert
        assertEquals(0, result.getExpiredCount());
        assertEquals(0, result.getWarningCount());
    }

}