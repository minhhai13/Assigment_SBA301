package com.minhhai.newsmanagement.service.impl;

import com.minhhai.newsmanagement.dto.response.NewsResponse;
import com.minhhai.newsmanagement.entity.Category;
import com.minhhai.newsmanagement.entity.NewsArticle;
import com.minhhai.newsmanagement.entity.UserAccount;
import com.minhhai.newsmanagement.repository.CategoryRepository;
import com.minhhai.newsmanagement.repository.NewsArticleRepository;
import com.minhhai.newsmanagement.repository.UserAccountRepository;
import com.minhhai.newsmanagement.service.NewsService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsArticleRepository newsArticleRepository;
    private final CategoryRepository categoryRepository;
    private final UserAccountRepository userAccountRepository;

    public NewsServiceImpl(NewsArticleRepository newsArticleRepository,
                           CategoryRepository categoryRepository,
                           UserAccountRepository userAccountRepository) {
        this.newsArticleRepository = newsArticleRepository;
        this.categoryRepository = categoryRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public List<NewsResponse> getAllNews() {
        return newsArticleRepository.findAll().stream().map(NewsResponse::from).toList();
    }

    @Override
    public NewsResponse getNewsById(Long id) {
        return newsArticleRepository.findById(id).map(NewsResponse::from).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


    @Override
    public NewsResponse createNews(NewsArticle article) {
        if (article.getTitle() == null || article.getCategory() == null || article.getAuthor() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request: Thiếu title, category hoặc author");
        }
        Long catId = article.getCategory().getId();
        Long authorId = article.getAuthor().getId();
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with id " + catId + " not found"));
        UserAccount author = userAccountRepository.findById(authorId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author with id " + authorId + " not found"));
        article.setCategory(category);
        article.setAuthor(author);
        if (article.getStatus() == null) article.setStatus(1);
        NewsArticle saved = newsArticleRepository.save(article);
        return NewsResponse.from(saved);
    }

    @Override
    public NewsResponse updateNews(Long id, NewsArticle request) {
        NewsArticle article = newsArticleRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết id: " + id));
        if (request.getTitle() != null) article.setTitle(request.getTitle());
        if (request.getTags() != null) article.setTags(request.getTags());
        if (request.getStatus() != null) article.setStatus(request.getStatus());
        if (request.getCategory() != null && request.getCategory().getId() != null) {
            Category category = categoryRepository.findById(request.getCategory().getId()).orElse(null);
            if (category != null) article.setCategory(category);
        }
        if (request.getAuthor() != null && request.getAuthor().getId() != null) {
            UserAccount author = userAccountRepository.findById(request.getAuthor().getId()).orElse(null);
            if (author != null) article.setAuthor(author);
        }
        NewsArticle saved = newsArticleRepository.save(article);
        return NewsResponse.from(saved);
    }

    @Override
    public void deleteNews(Long id) {
        if (!newsArticleRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết để xóa!");
        }
        newsArticleRepository.deleteById(id);
    }
}
