package com.example.itemmanagement.mapper;

import com.example.itemmanagement.entity.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UsersMapper {

    Users findByLoginId(@Param("loginId") String loginId);
    
 // 追加：ユーザー登録用
    void insertUser(Users user);
    
 //mail送信   
    String findEmailById(@Param("id") Integer id);

// PUSH通知用：全ユーザーIDを取得
    List<Integer> findAllUserIds();

}