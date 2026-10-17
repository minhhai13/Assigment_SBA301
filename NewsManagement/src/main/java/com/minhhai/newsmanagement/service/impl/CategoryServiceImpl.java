package com.minhhai.newsmanagement.service.impl;

import com.minhhai.newsmanagement.dto.request.CategoryRequest;
import com.minhhai.newsmanagement.dto.response.CategoryResponse;
import com.minhhai.newsmanagement.entity.Category;
import com.minhhai.newsmanagement.repository.CategoryRepository;
import com.minhhai.newsmanagement.service.CategoryService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream().map(CategoryResponse::from).toList();
    }

    @Override
    public CategoryResponse findCategoryById(Long id) {
        return categoryRepository.findById(id).map(CategoryResponse::from).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Không tìm thấy category với id: " + id
        ));
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        category.setStatus(request.status() != null ? request.status() : 1);

        if (request.parentCategoryId() != null) {
            Category parent = categoryRepository.findById(request.parentCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy parent category"));
            category.setParentCategory(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return CategoryResponse.from(savedCategory);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy category với id: " + id));

        category.setName(request.name());
        category.setDescription(request.description());
        category.setStatus(request.status() != null ? request.status() : category.getStatus());

        if (request.parentCategoryId() != null) {
            Category parent = categoryRepository.findById(request.parentCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy parent category"));
            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        return CategoryResponse.from(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy category với id: " + id));

        // Bạn có thể thêm logic kiểm tra xem category này có chứa bài viết hoặc category con nào không trước khi xoá.
        categoryRepository.delete(category);
    }
}
