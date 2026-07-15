package com.example.itemmanagement.service;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.form.AddItemForm;
import com.example.itemmanagement.mapper.ItemMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddItemServiceTest {

    @Mock
    private ItemMapper mapper;

    @InjectMocks
    private AddItemService sut;
    private AddItemForm form;
    private Integer userId;
    private int id;

    @BeforeEach
    void setUp() {

        form = createForm();
        userId = 1;
        id = 1;

    }

    @Test
    void add_FormをEntityへ変換しMapperを呼び出せること() {

        // Arrange
        ArgumentCaptor<Items> captor = ArgumentCaptor.forClass(Items.class);

        // Act
        sut.add(form, userId);

        // Assert
        verify(mapper, times(1)).add(captor.capture());

        Items actual = captor.getValue();

        assertEquals("鶏むね肉", actual.getName());
        assertEquals(1, actual.getCategoryId());
        assertEquals("300g", actual.getAmount());
        assertEquals(LocalDate.of(2026, 7, 20), actual.getDeadline());
        assertEquals(LocalDate.of(2026, 7, 14), actual.getPurchaseDate());
        assertEquals("テスト", actual.getOthers());
        assertTrue(actual.isFavorite());
        assertEquals(1, actual.getStatus());
        assertEquals(userId, actual.getUserId());

    }

    @Test
    void add_Mapper例外時に例外が送出されること() {


        // Arrange
        doThrow(new RuntimeException()) //Mockに例外を返すように設定
                .when(mapper).add(any(Items.class));

        // Act & Assert
        assertThrows(RuntimeException.class,    //sut.addを呼び出した時に、例外が発生するかの確認
                () -> sut.add(form, userId));

        verify(mapper, times(1))    //きちんとmapper.addが呼ばれて例外を出したかを確認するため
                .add(any(Items.class));

    }

    @Test
    void markAsPurchased_Mapperを呼び出せること() {

        // Arrange
        // なし

        // Act
        sut.markAsPurchased(id, userId);

        //Assert
        verify(mapper, times(1)).deleteFromShoppingList(id, userId);
    }

    @Test
    void markAsPurchased_Mapper例外時に例外が送出されること() {


        // Arrange
        doThrow(new RuntimeException()).when(mapper).deleteFromShoppingList(id, userId);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> sut.markAsPurchased(id, userId));

        verify(mapper, times(1)).deleteFromShoppingList(id, userId);

    }


    private AddItemForm createForm() {              //共通メソッド
        AddItemForm form = new AddItemForm();
        form.setName("鶏むね肉");
        form.setCategoryId(1);
        form.setAmount("300g");
        form.setDeadline(LocalDate.of(2026, 7, 20));
        form.setPurchaseDate(LocalDate.of(2026, 7, 14));
        form.setOthers("テスト");
        form.setFavorite(true);
        return form;
    }


}