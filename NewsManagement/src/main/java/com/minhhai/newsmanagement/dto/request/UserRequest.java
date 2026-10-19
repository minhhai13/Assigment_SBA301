package com.minhhai.newsmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank(message = "Username không được để trống")
        String username,

        String password,

        @NotBlank(message = "Họ và tên không được để trống")
        String name,

        Integer role,

        Integer status
) {
}
