package com.minhhai.newsmanagement.dto.response;

import com.minhhai.newsmanagement.entity.Category;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        Integer status,
        Long parentCategoryId
) {
    public static CategoryResponse from(Category c) {
        Long parentId = c.getParentCategory() != null ? c.getParentCategory().getId() : null;
        return new CategoryResponse(
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.getStatus(),
                parentId
        );
    }
}
