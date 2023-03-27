package com.example.crawlers.service.impl;

import com.example.crawlers.dao.CategoryDao;
import com.example.crawlers.model.Category;
import com.example.crawlers.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryDao categoryDao;

    @Autowired
    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public void save(Category category) {
        categoryDao.insert(category);
    }

    @Override
    public Category findByUrl(String url) {
        return categoryDao.selectByUrl(url);
    }
}
