package com.example.itemmanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.itemmanagement.mapper.PushSubscriptionMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PushSubscriptionService {

    private final PushSubscriptionMapper pushSubscriptionMapper;

    @Transactional
    public void saveSubscription(
            Integer userId,
            String endpoint,
            String p256dh,
            String auth,
            String userAgent) {

        pushSubscriptionMapper.insertSubscription(
                userId,
                endpoint,
                p256dh,
                auth,
                userAgent
        );
    }
}