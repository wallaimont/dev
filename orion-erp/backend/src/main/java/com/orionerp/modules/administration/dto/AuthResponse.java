package com.orionerp.modules.administration.dto;

public record AuthResponse(
        String tokenType,
        String accessToken,
        String refreshToken,
        long expiresIn,
        AuthUserDto usuario
) {
}
