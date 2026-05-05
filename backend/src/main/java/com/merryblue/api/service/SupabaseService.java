package com.merryblue.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SupabaseService {

    private final WebClient.Builder webClientBuilder;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String serviceRoleKey;

    /**
     * Example method to create a user via Supabase Admin API
     */
    public Mono<Map> adminCreateUser(String email, String password, Map<String, Object> metadata) {
        WebClient webClient = webClientBuilder.baseUrl(supabaseUrl).build();

        return webClient.post()
                .uri("/auth/v1/admin/users")
                .header("Authorization", "Bearer " + serviceRoleKey)
                .header("apikey", serviceRoleKey)
                .bodyValue(Map.of(
                        "email", email,
                        "password", password,
                        "user_metadata", metadata,
                        "email_confirm", true
                ))
                .retrieve()
                .bodyToMono(Map.class);
    }
}
