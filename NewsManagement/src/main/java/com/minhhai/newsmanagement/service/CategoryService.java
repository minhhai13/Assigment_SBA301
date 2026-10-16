package com.minhhai.newsmanagement.service;

import com.minhhai.newsmanagement.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    abstract List<CategoryResponse> getCategories();

    CategoryResponse findCategoryById(Long id);
}
