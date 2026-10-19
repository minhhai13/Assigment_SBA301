package com.minhhai.newsmanagement.service.impl;

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
import java.util.Map;

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
    public List<NewsArticle> getAllNews() {
        return newsArticleRepository.findAll();
    }

    @Override
    public NewsArticle getNewsById(Long id) {
        return newsArticleRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "không tìm thấy news với id" + id));
    }

    @Override
    public NewsArticle createNews(Map<String, Object> body) {
        if (!body.containsKey("categoryId") || !body.containsKey("authorId") || !body.containsKey("title")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        Long categoryId = Long.valueOf(body.get("categoryId").toString());
        Long authorId = Long.valueOf(body.get("authorId").toString());

        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy category với id" + categoryId));

        UserAccount author = userAccountRepository.findById(authorId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy author với id" + authorId));

        NewsArticle article = new NewsArticle();
        article.setTitle(body.get("title").toString());
        article.setTags(body.getOrDefault("tags", "").toString());
        article.setStatus(body.containsKey("status") ? Integer.valueOf(body.get("status").toString()) : 1);
        article.setCategory(category);
        article.setAuthor(author);

        return newsArticleRepository.save(article);
    }

    @Override
    public NewsArticle updateNews(Long id, Map<String, Object> body) {
        NewsArticle article = newsArticleRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết id: " + id));

        if (body.containsKey("title")) article.setTitle(body.get("title").toString());
        if (body.containsKey("tags")) article.setTags(body.getOrDefault("tags", "").toString());
        if (body.containsKey("status")) article.setStatus(Integer.valueOf(body.get("status").toString()));

        if (body.containsKey("categoryId")) {
            Long categoryId = Long.valueOf(body.get("categoryId").toString());
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if (category != null) article.setCategory(category);
        }

        if (body.containsKey("authorId")) {
            Long authorId = Long.valueOf(body.get("authorId").toString());
            UserAccount author = userAccountRepository.findById(authorId).orElse(null);
            if (author != null) article.setAuthor(author);
        }

        return newsArticleRepository.save(article);
    }

    @Override
    public void deleteNews(Long id) {
        if (!newsArticleRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết để xóa!");
        }
        newsArticleRepository.deleteById(id);
    }
}
