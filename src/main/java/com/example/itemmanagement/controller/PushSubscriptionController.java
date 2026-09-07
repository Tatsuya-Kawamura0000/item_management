package com.example.itemmanagement.controller;

import com.example.itemmanagement.config.VapidProperties;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.PushSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PushSubscriptionController {

    @Autowired
    private PushSubscriptionService pushSubscriptionService;

    @Autowired
    private VapidProperties vapidProperties;

    @PostMapping("/api/push/subscribe")
    public ResponseEntity<Void> subscribe(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestBody PushSubscriptionRequest request) {

        pushSubscriptionService.saveSubscription(
                loginUser.getId(),
                request.getEndpoint(),
                request.getP256dh(),
                request.getAuth(),
                request.getUserAgent()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/push/unsubscribe")
    public ResponseEntity<Void> unsubscribe(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestBody PushUnsubscribeRequest request) {

        if (loginUser != null && request.getEndpoint() != null) {
            pushSubscriptionService.deleteSubscription(loginUser.getId(), request.getEndpoint());
        }

        return ResponseEntity.ok().build();
    }

    public static class PushUnsubscribeRequest {
        private String endpoint;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }
    }

    public static class PushSubscriptionRequest {

        private String endpoint;
        private String p256dh;
        private String auth;
        private String userAgent;

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

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }
    }
    @GetMapping("/api/push/public-key")
    public ResponseEntity<String> getPublicKey() {
        return ResponseEntity.ok(vapidProperties.getPublicKey());
    }

}