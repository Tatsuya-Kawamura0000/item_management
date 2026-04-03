package com.example.itemmanagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()   // 認証必須
            )
            .formLogin(form -> form
                .defaultSuccessUrl("/users", true)
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 本番・DB認証用
        return new BCryptPasswordEncoder();

        // InMemory用（テスト）：
        // return org.springframework.security.crypto.password.PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // DB認証用：UserDetailsServiceImpl が @Service で登録済みなので不要
    // InMemory認証用（テスト）はコメントアウト
    /*
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("test")
                .password(passwordEncoder().encode("1234")) // ハッシュ化済み
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
    */
}