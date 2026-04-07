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

    
//@Scheduled(cron = "0 */1 * * * *")    テスト用 
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

            String subject = "【期限通知】食品の期限が近づいています";

            StringBuilder text = new StringBuilder();
            text.append("次の食品の期限が近づいています。\n\n");

            for (Items item : userItems) {
                text.append("食品名: ")
                    .append(item.getName())
                    .append("\n期限: ")
                    .append(item.getDeadline())
                    .append("\n\n");
            }

            mailService.sendMail(email, subject, text.toString());
        }
    }
}
