package com.minhhai.newsmanagement.dto.response;

import com.minhhai.newsmanagement.entity.NewsArticle;

public record NewsResponse(
        Long id,
        String title,
        String tags,
        Integer status,
        Long categoryId,
        String categoryName,
        Long authorId,
        String authorName
) {
    public static NewsResponse from(NewsArticle article) {
        return new NewsResponse(
                article.getId(),
                article.getTitle(),
                article.getTags(),
                article.getStatus(),
                article.getCategory() != null ? article.getCategory().getId() : null,
                article.getCategory() != null ? article.getCategory().getName() : null,
                article.getAuthor() != null ? article.getAuthor().getId() : null,
                article.getAuthor() != null ? article.getAuthor().getName() : null
        );
    }
}
