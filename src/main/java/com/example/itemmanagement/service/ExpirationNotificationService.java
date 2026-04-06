package com.example.itemmanagement.service;

import java.util.List;

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

    
//@Scheduled(cron = "0 */1 * * * *")     
@Scheduled(cron = "0 0 9 * * *")  // 毎日9時に実行
    public void notifyExpiringItems() {

        List<Items> items = itemMapper.findExpiringItems();

        for (Items item : items) {

            String email = usersMapper.findEmailById(item.getUserId());

            String subject = "【期限通知】食品の期限が近づいています";

            String text =
                    "次の食品の期限が近づいています。\n\n" +
                    "食品名: " + item.getName() + "\n" +
                    "期限: " + item.getDeadline();

            mailService.sendMail(email, subject, text);
        }
    }
}
