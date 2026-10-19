package com.minhhai.newsmanagement.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
        @NotNull(message = "Name is required")
        String name,
        String description,
        @NotNull(message = "Status is required")
        @Min(value = 0, message = "Status must be 0 or 1")
        @Max(value = 1, message = "Status must be 0 or 1")
        Integer status,
        Long parentCategoryId
) {
}