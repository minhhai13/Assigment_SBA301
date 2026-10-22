package com.minhhai.newsmanagement.service;

import com.minhhai.newsmanagement.dto.response.NewsResponse;
import com.minhhai.newsmanagement.entity.NewsArticle;

import java.util.List;

public interface NewsService {
    List<NewsResponse> getAllNews();

    NewsResponse getNewsById(Long id);

    NewsResponse createNews(NewsArticle newsArticle);

    NewsResponse updateNews(Long id, NewsArticle request);

    void deleteNews(Long id);
}
