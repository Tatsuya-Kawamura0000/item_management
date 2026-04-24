package com.example.itemmanagement.mapper;

import com.example.itemmanagement.entity.Items;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemSearchMapper {

        //アイテム検索
        List<Items> searchItems(@Param("searchType") String searchType,
                                @Param("keyword") String keyword,
                                @Param("userId") Integer userId);

}
