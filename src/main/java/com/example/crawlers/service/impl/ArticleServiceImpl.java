package com.example.crawlers.service.impl;

import com.example.crawlers.dao.ArticleDao;
import com.example.crawlers.model.Article;
import com.example.crawlers.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleDao articleDao;

    @Autowired
    public ArticleServiceImpl(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    @Override
    public void save(Article article) {
        articleDao.insert(article);
    }
}
