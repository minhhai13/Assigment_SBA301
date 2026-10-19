package com.minhhai.newsmanagement.service;

import com.minhhai.newsmanagement.entity.NewsArticle;

import java.util.List;
import java.util.Map;

public interface NewsService {
    List<NewsArticle> getAllNews();

    NewsArticle getNewsById(Long id);

    NewsArticle createNews(Map<String, Object> body);

    NewsArticle updateNews(Long id, Map<String, Object> body);

    void deleteNews(Long id);
}
