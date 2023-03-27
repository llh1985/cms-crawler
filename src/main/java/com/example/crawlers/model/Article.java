package com.example.crawlers.model;

import lombok.Data;

@Data
public class Article {

    private Integer id;
    private String title;
    private String content;
    private String url;
    private String publishDate;
    private Integer categoryId;
    // 省略 getter 和 setter 方法
}
