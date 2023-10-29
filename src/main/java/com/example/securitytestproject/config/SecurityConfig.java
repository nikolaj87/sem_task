package com.example.securitytestproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean //- Этот аннотированный метод создает и возвращает объект SecurityFilterChain.
    public SecurityFilterChain defaultSecurity(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/").permitAll()
                        .requestMatchers(HttpMethod.POST,"/clients/create-user").permitAll()
                        .requestMatchers(HttpMethod.POST,"/clients//{id}").authenticated()
                        .requestMatchers(HttpMethod.POST,"/clients/createManager").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/clients/{/name}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/clients/role-{/role}").hasRole("ADMIN")
                        .anyRequest().permitAll() // -это тоже не особо надо,так как у меня нету более котроллеров
                )
                .csrf(AbstractHttpConfigurer::disable) // -Отключает CSRF (Cross Site Request Forgery) защиту. CSRF - это тип атаки на веб-приложения, и Spring Security по умолчанию включает защиту от него.
                .build(); // - Завершает настройку и создает SecurityFilterChain объект, который будет использоваться Spring Security для обработки запросов.
    }
}
