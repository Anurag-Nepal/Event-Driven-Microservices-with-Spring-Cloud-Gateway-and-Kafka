package com.anurag.authServer.Sevice;

import com.anurag.authServer.DTO.AuthResponse;
import com.anurag.authServer.DTO.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class KeycloakTokenService {

    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.registration.keycloak-login.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak-login.client-secret}")
    private String clientSecret;
    public KeycloakTokenService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<AuthResponse> login(LoginRequest request) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("grant_type", "password");
        formData.add("username", request.username());
        formData.add("password", request.password());

        return webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::toAuthResponse)
                .onErrorResume(e -> Mono.error(new RuntimeException("Login failed: " + e.getMessage())));
    }

    public Mono<AuthResponse> refresh(String refreshToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);

        return webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::toAuthResponse)
                .onErrorResume(e -> Mono.error(new RuntimeException("Refresh failed: " + e.getMessage())));
    }

    private AuthResponse toAuthResponse(Map<String, Object> tokenResponse) {
        return new AuthResponse(
                (String) tokenResponse.get("access_token"),
                (String) tokenResponse.get("refresh_token"),
                (Integer) tokenResponse.get("expires_in"),
                (Integer) tokenResponse.get("refresh_expires_in"),
                (String) tokenResponse.get("token_type")
        );
    }
}