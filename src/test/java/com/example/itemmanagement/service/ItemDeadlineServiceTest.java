package com.example.itemmanagement.service;

import com.example.itemmanagement.entity.Items;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemDeadlineServiceTest {

    private final ItemDeadlineService sut = new ItemDeadlineService();

    @Test
    void 期限切れメッセージを設定できること() {

        // Arrange
        Items item = new Items();
        item.setDeadline(LocalDate.now().minusDays(1));

        List<Items> items = List.of(item);

        // Act
        sut.applyDeadlineMessage(items);

        // Assert
        assertEquals("期限切れです", item.getMessage());

    }

    @Test
    void 本日が賞味期限の場合に期限間近メッセージを設定できること() {

        // Arrange
        Items item = new Items();
        item.setDeadline(LocalDate.now());

        List<Items> items = List.of(item);

        // Act
        sut.applyDeadlineMessage(items);

        // Assert
        assertEquals("期限間近", item.getMessage());

    }

    @Test
    void 賞味期限が3日後の場合に期限間近フラグを設定できること() {

        // Arrange
        Items item = new Items();
        item.setDeadline(LocalDate.now().plusDays(3));

        List<Items> items = List.of(item);

        // Act
        sut.applyDeadlineMessage(items);

        // Assert
        assertEquals("期限間近", item.getMessage());
        assertTrue(item.isExpiringSoon());

    }

    @Test
    void 賞味期限が4日後の場合に空文字を設定できること() {

        // Arrange
        Items item = new Items();
        item.setDeadline(LocalDate.now().plusDays(4));

        List<Items> items = List.of(item);

        // Act
        sut.applyDeadlineMessage(items);

        // Assert
        assertEquals("", item.getMessage());

    }

    @Test
    void 賞味期限が未設定の場合に空文字を設定できること() {

        // Arrange
        Items item = new Items();

        List<Items> items = List.of(item);

        // Act
        sut.applyDeadlineMessage(items);

        // Assert
        assertEquals("", item.getMessage());
    }

}