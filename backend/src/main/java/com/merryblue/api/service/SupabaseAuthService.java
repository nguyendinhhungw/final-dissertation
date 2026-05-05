package com.merryblue.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupabaseAuthService {

    private final WebClient webClient;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon-key}")
    private String anonKey;

    public Mono<String> login(String email, String password) {
        log.info("Attempting login for email: {}", email);
        return webClient.post()
                .uri(supabaseUrl + "/auth/v1/token?grant_type=password")
                .header("apikey", anonKey)
                .bodyValue(Map.of("email", email, "password", password))
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<Void> logout(String accessToken) {
        log.info("Logging out user...");
        return webClient.post()
                .uri(supabaseUrl + "/auth/v1/logout")
                .header("apikey", anonKey)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
