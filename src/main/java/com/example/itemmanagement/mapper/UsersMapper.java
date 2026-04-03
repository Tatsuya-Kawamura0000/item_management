package com.example.itemmanagement.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.itemmanagement.entity.Users;

@Mapper
public interface UsersMapper {

    Users findByLoginId(@Param("loginId") String loginId);
    
 // 追加：ユーザー登録用
    void insertUser(Users user);

}