package com.anurag.authServer.DTO;


public record LoginRequest(
        String username,
        String password
) {}