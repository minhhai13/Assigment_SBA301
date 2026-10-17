package com.minhhai.newsmanagement.service;

import com.minhhai.newsmanagement.dto.request.CategoryRequest;
import com.minhhai.newsmanagement.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getCategories();
    CategoryResponse findCategoryById(Long id);
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
}
