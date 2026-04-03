package com.example.itemmanagement.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.itemmanagement.mapper.ItemMapper;

@Service
public class StopItemService {

    @Autowired 
    private ItemMapper mapper;

    /**
     * 指定IDかつユーザーIDのアイテムを論理削除（status=0）する
     */
    @Transactional 
    public void stopItem(int id, Integer userId) {
        mapper.stop(id, userId); // Mapper の stop(id, userId) を呼び出す
    }

}
