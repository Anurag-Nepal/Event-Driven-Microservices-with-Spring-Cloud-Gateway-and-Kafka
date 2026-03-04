package com.anurag.authServer.DTO;

public record RegisterRequest(
        String username,
        String email,
        String password,
        String firstName,
        String lastName
) {}
