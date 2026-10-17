package com.minhhai.newsmanagement.dto.request;

public record CategoryRequest(
        String name,
        String description,
        Integer status,
        Long parentCategoryId
) {
}