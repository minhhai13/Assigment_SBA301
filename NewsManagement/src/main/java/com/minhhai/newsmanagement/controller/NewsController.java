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
@RequestMapping("/api/news")
public class NewsController {

    @GetMapping
    public ResponseEntity<String> getNews() {
        return notImplemented();
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getNewsById(@PathVariable Long id) {
        return notImplemented();
    }

    @PostMapping
    public ResponseEntity<String> createNews() {
        return notImplemented();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateNews(@PathVariable Long id) {
        return notImplemented();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNews(@PathVariable Long id) {
        return notImplemented();
    }

    private ResponseEntity<String> notImplemented() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Chưa implement logic");
    }
}
