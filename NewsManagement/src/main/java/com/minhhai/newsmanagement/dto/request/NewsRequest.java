package com.minhhai.newsmanagement.dto.request;

public record NewsRequest(
        String title,
        String tags,
        Integer status,
        Long categoryId,
        Long authorId
) {
}
