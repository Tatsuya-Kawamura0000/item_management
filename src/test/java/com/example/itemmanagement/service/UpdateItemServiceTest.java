package com.example.itemmanagement.service;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.mapper.ItemMapper;
import com.example.itemmanagement.mapper.ShoppingListMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateItemServiceTest {

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private ShoppingListMapper slMapper;

    @InjectMocks
    private UpdateItemService sut;

    @Test
    void updateItem_更新対象のidとuserIdを設定してMapperを呼び出せること() {

        //Arrange
        Items item = createItem();
        Integer userId = item.getUserId();
        int id = item.getId();

        ArgumentCaptor<Items> captor = ArgumentCaptor.forClass(Items.class);

        //Act
        sut.updateItem(id, userId, item);

        //Assert
        verify(itemMapper, times(1)).update(captor.capture());

        Items actual = captor.getValue();
        assertEquals(id, actual.getId());
        assertEquals(userId, actual.getUserId());

    }

    @Test
    void updateFavorite_userIdを設定してMapperを呼び出せること() {

        //Arrange
        Items item = createItem();
        Integer userId = item.getUserId();

        ArgumentCaptor<Items> captor = ArgumentCaptor.forClass(Items.class);

        //Act
        sut.updateFavorite(item, userId);

        //Assert
        verify(itemMapper, times(1)).updateFavorite(captor.capture());

        Items actual = captor.getValue();
        assertEquals(userId, actual.getUserId());

    }

    @Test
    void bulkDelete_リストの中身の数だけMapperが呼び出せること() {

        //Arrange
        Integer userId = 999;

        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(2);
        ids.add(3);
        ids.add(4);
        ids.add(5);

        //Act
        sut.bulkDelete(ids, userId);

        //Assert
        verify(slMapper, times(ids.size()))
                .stop(anyInt(), eq(userId));
    }


    @Test
    void bulkDeleteFromItems_リストの中身の数だけMapperが呼び出せること() {

        //Arrange
        Integer userId = 999;

        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(2);
        ids.add(3);
        ids.add(4);
        ids.add(5);

        //Act
        sut.bulkDeleteFromItems(ids, userId);

        //Assert
        verify(itemMapper, times(ids.size()))
                .stop(anyInt(), eq(userId));

    }


    private Items createItem() {              //共通メソッド
        //選択されたアイテムを用意する
        Items item = new Items();
        item.setId(99);
        item.setName("りんご");
        item.setCategoryId(99);
        item.setAmount("1個");
        item.setDeadline(LocalDate.of(2026, 7, 20));
        item.setPurchaseDate(LocalDate.of(2026, 7, 14));
        item.setOthers("");
        item.setStatus(1);
        item.setCategoryName("果物");
        item.setExpiringSoon(false);
        item.setFavorite(false);
        item.setMessage("");
        item.setUserId(999);
        return item;
    }

}