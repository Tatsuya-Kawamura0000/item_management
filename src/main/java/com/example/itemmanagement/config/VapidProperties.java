package com.example.itemmanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Web Push の VAPID 鍵を環境変数から読み込む。
 * 秘密鍵はソースコードや Git には置かない。
 */
@Component
public class VapidProperties {

    // フロントへ渡す公開鍵
    @Value("${vapid.public-key:}")
    private String publicKey;

    // サーバーだけが使う秘密鍵
    @Value("${vapid.private-key:}")
    private String privateKey;

    // Push サーバーへ示す連絡先（mailto: または https URL）
    @Value("${vapid.subject:}")
    private String subject;

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getSubject() {
        return subject;
    }

    /**
     * 鍵が揃っていないときは Push 機能を使わず、アプリ本体は通常どおり動かす。
     */
    public boolean isConfigured() {
        return hasText(publicKey) && hasText(privateKey) && hasText(subject);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
