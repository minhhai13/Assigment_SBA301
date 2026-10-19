package com.minhhai.newsmanagement.dto.response;

import com.minhhai.newsmanagement.entity.UserAccount;

public record UserResponse(
        Long id,
        String username,
        String name,
        Integer role,
        Integer status
) {
    public static UserResponse from(UserAccount user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getRole(),
                user.getStatus()
        );
    }
}
