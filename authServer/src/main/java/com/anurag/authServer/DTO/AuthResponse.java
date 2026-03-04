package com.anurag.authServer.DTO;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        int expiresIn,
        int refreshExpiresIn,
        String tokenType
) {}