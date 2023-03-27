package com.example.crawlers.dao.impl;

import com.example.crawlers.dao.CategoryDao;
import com.example.crawlers.model.Category;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryDaoImpl implements CategoryDao {
    private final SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    public CategoryDaoImpl(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    @Override
    public void insert(Category category) {
        sqlSessionTemplate.insert("com.example.crawlers.dao.CategoryDao.insert", category);
    }

    @Override
    public Category selectByUrl(String url) {
        return sqlSessionTemplate.selectOne("com.example.crawlers.dao.CategoryDao.selectByUrl", url);
    }
}
