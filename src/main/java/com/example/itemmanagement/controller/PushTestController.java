package com.example.itemmanagement.controller;

import com.example.itemmanagement.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PushTestController {

    private final PushNotificationService pushNotificationService;

    @PostMapping("/api/push/test")
    public ResponseEntity<String> sendTest(
            @RequestParam Integer userId) {

        try {
            pushNotificationService.sendTestNotification(userId);
            return ResponseEntity.ok("Push通知を送信しました。");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Push通知の送信に失敗しました: " + e.getMessage());
        }
    }
}