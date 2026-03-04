package com.anurag.authServer.Sevice;
import com.anurag.authServer.DTO.RegisterRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class KeycloakAdminService {

    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.registration.keycloak-admin.client-id}")
    private String adminClientId;

    @Value("${spring.security.oauth2.client.registration.keycloak-admin.client-secret}")
    private String adminClientSecret;

    @Value("${keycloak.admin.users-url}")
    private String adminUsersUrl;

    public KeycloakAdminService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> register(RegisterRequest request) {
        return getAdminAccessToken()
                .flatMap(adminToken -> createUser(adminToken, request))
                .map(response -> "User created successfully");
    }

    private Mono<String> getAdminAccessToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", adminClientId);
        formData.add("client_secret", adminClientSecret);
        formData.add("grant_type", "client_credentials");

        return webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("access_token"))
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed to obtain admin token: " + e.getMessage())));
    }

    private Mono<Void> createUser(String adminToken, RegisterRequest request) {
        Map<String, Object> user = Map.of(
                "username", request.username(),
                "email", request.email(),
                "firstName", request.firstName(),
                "lastName", request.lastName(),
                "enabled", true,
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", request.password(),
                        "temporary", false
                ))
        );

        return webClient.post()
                .uri(adminUsersUrl)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}