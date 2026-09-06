package com.example.itemmanagement.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PushNotificationMapper {

    /**
     * 通知処理の開始を記録する。
     * 同一ユーザー・同一日の重複実行を防ぐ。
     */
    int insertProcessingLog(
            @Param("userId") Integer userId
    );

    /**
     * 通知成功時に sent に更新する。
     */
    int updateSent(
            @Param("userId") Integer userId
    );

    /**
     * 通知失敗時に failed に更新する。
     */
    int updateFailed(
            @Param("userId") Integer userId
    );
}