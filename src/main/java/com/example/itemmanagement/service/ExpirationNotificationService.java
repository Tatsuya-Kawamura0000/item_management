package com.example.itemmanagement.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

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
    private final SpringTemplateEngine templateEngine;

    @Value("${app.url}")
    private String appUrl;

    //@Scheduled(cron = "0 */1 * * * *") // テスト用
    @Scheduled(cron = "0 0 11,17 * * *", zone = "Asia/Tokyo")
    public void notifyExpiringItems() {

        List<Items> items = itemMapper.findExpiringItems();

        if (items.isEmpty()) {
            return;
        }

        Map<Integer, List<Items>> itemsByUser = new HashMap<>();

        for (Items item : items) {
            itemsByUser
                    .computeIfAbsent(item.getUserId(), k -> new ArrayList<>())
                    .add(item);
        }

        for (Map.Entry<Integer, List<Items>> entry : itemsByUser.entrySet()) {

            Integer userId = entry.getKey();
            List<Items> userItems = entry.getValue();

            String email = usersMapper.findEmailById(userId);

            if (email == null || email.isBlank()) {
                continue;
            }

            String subject = "【期限通知】食品の期限をお知らせします";

            List<Items> expiredItems = new ArrayList<>();
            List<Items> expiringItems = new ArrayList<>();

            for (Items item : userItems) {

                if (item.getDeadline().isBefore(LocalDate.now())) {
                    expiredItems.add(item);
                } else {
                    expiringItems.add(item);
                }

            }

            // テンプレートにデータを渡す
            Context context = new Context();
            context.setVariable("expiredItems", expiredItems);
            context.setVariable("expiringItems", expiringItems);
            context.setVariable("appUrl", appUrl);

            String html = templateEngine.process(
                    "mail/expiration-notice",
                    context
            );

            //mailService.sendHtmlMail(email, subject, html);　SMTP用
            mailService.sendHtmlMailBySendGrid(email, subject, html);  //SendGrid API
        }
    }

   /* @PostConstruct     テスト送信用　　起動時にメール送信できる
    public void testSend() {
        mailService.sendHtmlMailBySendGrid(
                "tatsuya.k89.0523kitaq17@gmail.com",
                "テスト送信",
                "<h1>SendGrid成功！</h1>"
        );
    }*/

}