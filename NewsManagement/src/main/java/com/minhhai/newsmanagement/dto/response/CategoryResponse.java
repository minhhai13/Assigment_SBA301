package com.minhhai.newsmanagement.dto.response;

import com.minhhai.newsmanagement.entity.Category;
import com.minhhai.newsmanagement.entity.NewsArticle;

import java.util.List;

public record CategoryResponse(String name, String description, Integer status, Category parentCategory,
                               List<Category> childCategories) {
    public static CategoryResponse from(Category c){
        return new CategoryResponse(c.getName(), c.getDescription(), c.getStatus(), c.getParentCategory(),c.getChildCategories());
    }
}
