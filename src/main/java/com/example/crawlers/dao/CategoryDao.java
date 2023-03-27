package com.example.crawlers.dao;

import com.example.crawlers.model.Category;

public interface CategoryDao {
    void insert(Category category);

    Category selectByUrl(String url);
}
