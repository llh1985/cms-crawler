package com.example.crawlers.service;

import com.example.crawlers.model.Category;

public interface CategoryService {
    void save(Category category);

    Category findByUrl(String url);
}
