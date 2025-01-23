package com.grocery.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // Disable CSRF protection
                .authorizeRequests()
                .requestMatchers("/**").permitAll()  // Allow unauthenticated access to all URLs
                .anyRequest().authenticated()         // Require authentication for other endpoints
                .and()
                .formLogin()
                .and()
                .httpBasic();
        return http.build();
    }
}
