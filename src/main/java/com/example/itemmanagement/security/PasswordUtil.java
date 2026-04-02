package com.example.itemmanagement.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtil {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    // パスワードをハッシュ化して返す
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    // テスト用: 確認
    public static void main(String[] args) {
        String raw = "   ";
        String hashed = encode(raw);
        System.out.println("rawパス: " + raw);
        System.out.println("ハッシュ化: " + hashed);
    }
}