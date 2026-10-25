package com.minhhai.newsmanagement.dto;

import jakarta.validation.constraints.NotBlank;

/** Form tạo/sửa bài: không có status/author, các giá trị đó do backend gán. */
public record ArticleRequest(
        @NotBlank(message = "Tiêu đề không được để trống") String title,
        String headline,
        String content,
        String tags,
        Long categoryId,
        Long version) {
}
