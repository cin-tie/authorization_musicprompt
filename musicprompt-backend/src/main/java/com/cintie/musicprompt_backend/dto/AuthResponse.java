package com.cintie.musicprompt_backend.dto;

public record AuthResponse(
        String token,
        String refreshToken,
        String message
) {}