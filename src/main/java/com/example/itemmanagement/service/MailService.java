package com.example.itemmanagement.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${SENDGRID_API_KEY}")  //RENDER上に設定済み
    private String apiKey;

    public void sendHtmlMailBySendGrid(String to, String subject, String html) {

        Email from = new Email("apl.itemmanagement@gmail.com"); // 例：xxxxx@gmail.com
        Email toEmail = new Email(to);

        Content content = new Content("text/html", html);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

        } catch (Exception e) {
            throw new RuntimeException("SendGrid送信失敗", e);
        }
    }

}
