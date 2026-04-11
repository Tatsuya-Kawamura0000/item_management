package com.example.itemmanagement.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    // 既存：テキストメール
    public void sendMail(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    // 追加：HTMLメール
    public void sendHtmlMail(String to, String subject, String html) {

        int retry = 3;

        for (int i = 0; i < retry; i++) {
            try {

                MimeMessage message = mailSender.createMimeMessage();

                MimeMessageHelper helper =
                        new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(html, true);

                mailSender.send(message);
                return;

            } catch (Exception e) {

                if (i == retry - 1) {
                    throw new RuntimeException("メール送信失敗", e);
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}

            }
        }
    }
}
