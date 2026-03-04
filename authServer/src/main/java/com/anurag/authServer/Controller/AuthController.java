package com.anurag.authServer.Controller;

import com.anurag.authServer.DTO.AuthResponse;
import com.anurag.authServer.DTO.LoginRequest;
import com.anurag.authServer.DTO.RegisterRequest;
import com.anurag.authServer.Sevice.KeycloakAdminService;
import com.anurag.authServer.Sevice.KeycloakTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakAdminService adminService;
    private final KeycloakTokenService tokenService;

    @PostMapping("/register")
    public Mono<String> register(@RequestBody RegisterRequest request) {
        return adminService.register(request);
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(@RequestBody LoginRequest request) {
        return tokenService.login(request);
    }

    @PostMapping("/refresh")
    public Mono<AuthResponse> refresh(@RequestParam String refreshToken) {
        return tokenService.refresh(refreshToken);
    }
}