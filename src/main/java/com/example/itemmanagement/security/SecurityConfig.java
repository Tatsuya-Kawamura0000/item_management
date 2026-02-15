package com.example.itemmanagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
         .csrf(csrf -> csrf.disable())
         .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()   // 認証必須に変更
                        )
                  .formLogin(Customizer.withDefaults()); // ログイン画面を有効化          
        return http.build();
    }
	
}

