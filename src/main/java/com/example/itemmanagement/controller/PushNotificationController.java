package com.example.itemmanagement.controller;

import com.example.itemmanagement.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
public class PushNotificationController {

    private final PushNotificationService pushNotificationService;

    @Value("${push.notification.secret}")
    private String pushNotificationSecret;

    @PostMapping("/expiring-items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendExpiringItemsNotification(
            @RequestHeader("X-Push-Secret") String secret
    ) throws Exception {

        if (!pushNotificationSecret.equals(secret)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Unauthorized"
            );
        }

        pushNotificationService.sendAllUsersExpiringItemsNotification();
    }
}