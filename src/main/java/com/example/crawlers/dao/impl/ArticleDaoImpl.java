package com.example.crawlers.dao.impl;

import com.example.crawlers.dao.ArticleDao;
import com.example.crawlers.model.Article;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleDaoImpl implements ArticleDao {
    private final SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    public ArticleDaoImpl(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    @Override
    public void insert(Article article) {
        sqlSessionTemplate.insert("com.example.crawlers.dao.ArticleDao.insert", article);
    }
}
