package com.minhhai.newsmanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @GetMapping
    public ResponseEntity<String> getCategories() {
        return notImplemented();
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getCategoryById(@PathVariable Long id) {
        return notImplemented();
    }

    @PostMapping
    public ResponseEntity<String> createCategory() {
        return notImplemented();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id) {
        return notImplemented();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        return notImplemented();
    }

    private ResponseEntity<String> notImplemented() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Chưa implement logic");
    }
}
