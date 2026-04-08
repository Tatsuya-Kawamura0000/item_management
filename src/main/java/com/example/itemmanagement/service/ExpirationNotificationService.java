package com.example.itemmanagement.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.mapper.ItemMapper;
import com.example.itemmanagement.mapper.UsersMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpirationNotificationService {

    private final ItemMapper itemMapper;
    private final UsersMapper usersMapper;
    private final MailService mailService;

    
//@Scheduled(cron = "0 */1 * * * *")    //テスト用 
@Scheduled(cron = "0 0 9 * * *", zone = "Asia/Tokyo")
    public void notifyExpiringItems() {

        List<Items> items = itemMapper.findExpiringItems();

        // ユーザーごとにアイテムをまとめる
        Map<Integer, List<Items>> itemsByUser = new HashMap<>();

        for (Items item : items) {
            itemsByUser
                .computeIfAbsent(item.getUserId(), k -> new ArrayList<>())
                .add(item);
        }

        // ユーザーごとにメール送信
        for (Map.Entry<Integer, List<Items>> entry : itemsByUser.entrySet()) {

            Integer userId = entry.getKey();
            List<Items> userItems = entry.getValue();

            String email = usersMapper.findEmailById(userId);

            if (email == null || email.isBlank()) {
                continue;
            }

            String subject = "【期限通知】食品の期限をお知らせします";

            StringBuilder text = new StringBuilder();

            List<Items> expiredItems = new ArrayList<>();
            List<Items> expiringItems = new ArrayList<>();

            // ① 期限切れ / 期限間近 を分類
            for (Items item : userItems) {

                if (item.getDeadline().isBefore(java.time.LocalDate.now())) {
                    expiredItems.add(item);
                } else {
                    expiringItems.add(item);
                }

            }

            text.append("食品の期限をお知らせします。\n\n");

            // ② 期限切れ
            if (!expiredItems.isEmpty()) {

                text.append("【期限切れ】\n");

                for (Items item : expiredItems) {
                    text.append("食品名: ")
                        .append(item.getName())
                        .append("\n期限: ")
                        .append(item.getDeadline())
                        .append("\n\n");
                }
            }

            // ③ 期限間近
            if (!expiringItems.isEmpty()) {

                text.append("【期限間近】\n");

                for (Items item : expiringItems) {
                    text.append("食品名: ")
                        .append(item.getName())
                        .append("\n期限: ")
                        .append(item.getDeadline())
                        .append("\n\n");
                }
            }

            mailService.sendMail(email, subject, text.toString());
        }
    }
}
