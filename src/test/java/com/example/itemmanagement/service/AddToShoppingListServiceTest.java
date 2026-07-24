package com.example.itemmanagement.service;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.entity.ShoppingListItem;
import com.example.itemmanagement.form.AddShoppingListItemForm;
import com.example.itemmanagement.mapper.ItemMapper;
import com.example.itemmanagement.mapper.ShoppingListMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddToShoppingListServiceTest {

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private ShoppingListMapper slMapper;

    @InjectMocks
    private AddToShoppingListService sut;

    @Test
    void addItemToList_重複なしの場合に買い物リストへ追加できること() {

        //Arrange

        //選択された食材を再現
        Items item = createItem();
        Integer userId = item.getUserId();
        int id = item.getId();

        when(itemMapper.findById(id, userId)).thenReturn(item);
        //重複チェックでfalseを返す　→　exist にfalseを格納
        when(slMapper.existsByName(item.getName(), userId)).thenReturn(false);

        //Act
        List<String> result = sut.addItemToList(id, userId);

        //Assert
        verify(itemMapper, times(1)).findById(id, userId);
        verify(slMapper, times(1)).existsByName(item.getName(), userId);

        ArgumentCaptor<ShoppingListItem> captor = ArgumentCaptor.forClass(ShoppingListItem.class);
        verify(slMapper, times(1)).insert(captor.capture());

        ShoppingListItem actual = captor.getValue();

        // ShoppingListItemの値を確認
        assertEquals(id, actual.getItemId());
        assertEquals(userId, actual.getUserId());
        assertTrue(actual.isStatus());
        assertFalse(actual.isPurchasedFlg());
        assertEquals("りんご", actual.getName());
        assertEquals("1個", actual.getAmount());
        assertEquals(99, actual.getCategoryId());
        assertEquals("果物", actual.getCategoryName());
        assertEquals(LocalDate.of(2026, 7, 14), actual.getPurchaseDate());
        assertFalse(actual.isFavorite());
        // LocalDateTime.now()で設定されるためnullでないことのみ確認
        assertNotNull(actual.getAddedAt());
        //戻り値が空のList(重複食材なし)であることの確認
        assertTrue(result.isEmpty());

    }

    @Test
    void addItemToList_重複ありの場合食材名を返却し買い物リストへ追加しないこと() {

        //Arrange

        //選択された食材を再現
        Items item = createItem();
        Integer userId = item.getUserId();
        int id = item.getId();

        when(itemMapper.findById(id, userId)).thenReturn(item);
        //重複チェックでtrueを返す　→　exist にtrueを格納
        when(slMapper.existsByName(item.getName(), userId)).thenReturn(true);

        //Act
        List<String> result = sut.addItemToList(id, userId);

        //Assert

        //listに格納された重複アイテム名が返却されること
        assertEquals("りんご", result.get(0));

        // Mapperが呼ばれていること
        verify(itemMapper, times(1)).findById(id, userId);
        verify(slMapper, times(1)).existsByName(item.getName(), userId);

        //insertが呼ばれていないこと
        verify(slMapper, never()).insert(any(ShoppingListItem.class));

    }

    @Test
    void addItemToList_買い物リストへの登録時に例外が発生した場合に例外を呼び出し元へ送出すること() {

        //Arrange

        //選択された食材を再現
        Items item = createItem();
        Integer userId = item.getUserId();
        int id = item.getId();

        when(itemMapper.findById(id, userId)).thenReturn(item);
        when(slMapper.existsByName(item.getName(), userId)).thenReturn(false);

        //例外を返すように設定
        doThrow(new RuntimeException()).when(slMapper).insert(any(ShoppingListItem.class));

        //Act　& Assert

        //addItemToListを呼び出した際に、RuntimeExceptionが送出されること
        assertThrows(RuntimeException.class, () -> sut.addItemToList(id, userId));

        //Mapperが呼ばれていること
        verify(itemMapper).findById(id, userId);
        verify(slMapper).existsByName(item.getName(), userId);
        verify(slMapper, times(1)).insert(any(ShoppingListItem.class));

    }


    @Test
    void addNewItem_フォームの値を買い物リストへ登録できること() {

        //Arrange
        AddShoppingListItemForm form = createForm();
        Integer userId = 999;
        ArgumentCaptor<ShoppingListItem> captor = ArgumentCaptor.forClass(ShoppingListItem.class);

        //Act
        sut.addNewItem(form, userId);

        //Assert
        verify(slMapper, times(1)).insert(captor.capture());
        ShoppingListItem actual = captor.getValue();

        // entity化されたShoppingListItemの値を確認
        assertNull(actual.getItemId());             //idは、addNewItemでentity化する際、NULLがセット(自動採番するため)されるため
        assertEquals(userId, actual.getUserId());
        assertEquals("鶏むね肉", actual.getName());
        assertEquals("300g", actual.getAmount());
        assertTrue(actual.isStatus());
        assertFalse(actual.isPurchasedFlg());
        // LocalDateTime.now()で設定されるためnullでないことのみ確認
        assertNotNull(actual.getAddedAt());

    }


    @Test
    void getShoppingListCount_買い物リストの件数を取得できること() {

        //Arrange
        Integer userId = 999;
        when(slMapper.countByUserId(userId)).thenReturn(99);

        //Act
        int result = sut.getShoppingListCount(userId);

        //Assert
        verify(slMapper, times(1)).countByUserId(userId);

        assertEquals(99,result);

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

    private AddShoppingListItemForm createForm() {              //共通メソッド
        AddShoppingListItemForm form = new AddShoppingListItemForm();
        form.setName("鶏むね肉");
        form.setAmount("300g");
        return form;
    }

}