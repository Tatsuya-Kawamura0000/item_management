package com.example.itemmanagement.service;

import com.example.itemmanagement.config.VapidProperties;
import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.mapper.ItemMapper;
import com.example.itemmanagement.mapper.PushNotificationMapper;
import com.example.itemmanagement.mapper.PushSubscriptionMapper;
import com.example.itemmanagement.mapper.PushSubscriptionMapper.PushSubscription;
import com.example.itemmanagement.mapper.UsersMapper;
import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final PushSubscriptionMapper pushSubscriptionMapper;
    private final PushNotificationMapper pushNotificationMapper;
    private final VapidProperties vapidProperties;
    private final ItemMapper itemMapper;
    private final UsersMapper usersMapper;

    public void sendTestNotification(Integer userId) throws Exception {

        List<PushSubscription> subscriptions =
                pushSubscriptionMapper.findByUserId(userId);

        if (subscriptions.isEmpty()) {
            throw new IllegalStateException(
                    "PushSubscriptionが登録されていません。userId=" + userId
            );
        }

        PushService pushService = new PushService(
                vapidProperties.getPublicKey(),
                vapidProperties.getPrivateKey(),
                vapidProperties.getSubject()
        );

        String payload = """
                {
                    "title": "賞味期限管理",
                    "body": "Push通知のテストです。",
                    "data": {
                        "url": "/dashboard"
                    }
                }
                """;

        for (PushSubscription subscription : subscriptions) {

            Notification notification = new Notification(
                    subscription.getEndpoint(),
                    subscription.getP256dh(),
                    subscription.getAuth(),
                    payload
            );

            HttpResponse response = pushService.send(notification);

            int statusCode = response.getStatusLine().getStatusCode();

            System.out.println(
                    "Push送信結果: HTTP " + statusCode
            );

            if (statusCode == 410) {
                pushSubscriptionMapper.deleteByEndpoint(
                        subscription.getEndpoint()
                );

                System.out.println(
                        "無効なPush購読を削除しました: " +
                                subscription.getEndpoint()
                );
            }
        }
    }

    public List<Items> findExpiringItems(Integer userId) {
        return itemMapper.findExpiringItemsByUserId(userId);
    }

    private String buildExpiringItemsMessage(List<Items> items) {

        StringBuilder message = new StringBuilder();

        message.append("賞味期限が近い商品があります\n\n");

        int displayCount = Math.min(items.size(), 5);

        for (int i = 0; i < displayCount; i++) {
            message.append("・")
                    .append(items.get(i).getName())
                    .append("\n");
        }

        if (items.size() > 5) {
            message.append("ほか")
                    .append(items.size() - 5)
                    .append("件\n");
        }

        message.append("\n")
                .append(items.size())
                .append("件の賞味期限が近づいています。");

        return message.toString();
    }

    public void sendExpiringItemsNotification(Integer userId) throws Exception {

        // ① 期限間近の商品を取得
        List<Items> expiringItems = findExpiringItems(userId);

        // 期限間近の商品がなければ通知しない
        if (expiringItems.isEmpty()) {
            return;
        }

        // ② ユーザーの今日の通知処理を確保
        //
        // 0件の場合：
        //   すでに今日通知済み、または別の処理が実行中
        //
        int claimed =
                pushNotificationMapper.insertProcessingLog(userId);

        if (claimed == 0) {
            System.out.println(
                    "本日の通知処理をスキップしました。userId=" + userId
            );
            return;
        }

        try {

            // ③ 通知本文を作成
            String message = buildExpiringItemsMessage(expiringItems);

            // ④ ユーザーの全購読を取得
            List<PushSubscription> subscriptions =
                    pushSubscriptionMapper.findByUserId(userId);

            // 購読がなければ通知しない
            if (subscriptions.isEmpty()) {
                pushNotificationMapper.updateFailed(userId);

                System.out.println(
                        "PushSubscriptionがないため通知をスキップしました。userId=" +
                                userId
                );
                return;
            }

            PushService pushService = new PushService(
                    vapidProperties.getPublicKey(),
                    vapidProperties.getPrivateKey(),
                    vapidProperties.getSubject()
            );

            String payload = """
                {
                    "title": "賞味期限管理アプリ",
                    "body": %s,
                    "data": {
                        "url": "/dashboard"
                    }
                }
                """.formatted(
                    toJsonString(message)
            );

            boolean sentSuccessfully = false;

            // ⑤ ユーザーの全端末へ送信
            for (PushSubscription subscription : subscriptions) {

                Notification notification = new Notification(
                        subscription.getEndpoint(),
                        subscription.getP256dh(),
                        subscription.getAuth(),
                        payload
                );

                HttpResponse response = pushService.send(notification);

                int statusCode = response.getStatusLine().getStatusCode();

                System.out.println(
                        "期限通知送信結果: HTTP " + statusCode
                );

                // 送信成功
                if (statusCode >= 200 && statusCode < 300) {
                    sentSuccessfully = true;
                }

                // ⑥ 無効な購読を削除
                if (statusCode == 410 || statusCode == 404) {

                    pushSubscriptionMapper.deleteByEndpoint(
                            subscription.getEndpoint()
                    );

                    System.out.println(
                            "無効なPush購読を削除しました: " +
                                    subscription.getEndpoint()
                    );
                }
            }

            // ⑦ 1端末以上への送信成功なら sent
            if (sentSuccessfully) {

                pushNotificationMapper.updateSent(userId);

                System.out.println(
                        "期限通知を送信済みにしました。userId=" + userId
                );

            } else {

                // 全端末への送信に失敗
                pushNotificationMapper.updateFailed(userId);

                System.out.println(
                        "期限通知の送信に失敗しました。userId=" + userId
                );
            }

        } catch (Exception e) {

            // 予期しないエラーの場合は failed に戻す
            pushNotificationMapper.updateFailed(userId);

            throw e;
        }
    }

    private String toJsonString(String value) {
        return "\"" +
                value
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r") +
                "\"";
    }

    public void sendAllUsersExpiringItemsNotification() throws Exception {

        List<Integer> userIds = usersMapper.findAllUserIds();

        for (Integer userId : userIds) {
            sendExpiringItemsNotification(userId);
        }
    }
}