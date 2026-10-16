package com.minhhai.newsmanagement.service.impl;

import com.minhhai.newsmanagement.dto.response.CategoryResponse;
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
}
