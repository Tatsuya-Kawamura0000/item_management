package com.example.itemmanagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // CSRF 無効化（必要に応じて）
            .authorizeHttpRequests()
                .anyRequest().permitAll(); // 全てのページを認証不要に
        return http.build();
    }
	
}

