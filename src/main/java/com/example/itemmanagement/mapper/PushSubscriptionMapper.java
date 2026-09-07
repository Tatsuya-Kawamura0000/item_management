package com.example.itemmanagement.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PushSubscriptionMapper {

    void insertSubscription(
            @Param("userId") Integer userId,
            @Param("endpoint") String endpoint,
            @Param("p256dh") String p256dh,
            @Param("auth") String auth,
            @Param("userAgent") String userAgent
    );

    List<PushSubscription> findByUserId(
            @Param("userId") Integer userId
    );

    void deleteByEndpoint(
            @Param("endpoint") String endpoint
    );

    void deleteByUserIdAndEndpoint(
            @Param("userId") Integer userId,
            @Param("endpoint") String endpoint
    );

    class PushSubscription {
        private String endpoint;
        private String p256dh;
        private String auth;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getP256dh() {
            return p256dh;
        }

        public void setP256dh(String p256dh) {
            this.p256dh = p256dh;
        }

        public String getAuth() {
            return auth;
        }

        public void setAuth(String auth) {
            this.auth = auth;
        }
    }
}