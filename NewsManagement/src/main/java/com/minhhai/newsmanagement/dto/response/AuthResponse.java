package com.minhhai.newsmanagement.dto.response;

public record AuthResponse(
        String token,
        String tokenType,
        Long id,
        String username,
        String name,
        Integer role,
        Integer status
) {
}
