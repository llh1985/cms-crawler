package com.example.crawlers.model;

import lombok.Data;

@Data
public class Category {
    private Integer id;
    private String name;
    private String url;
    private String parentName;
    private String type;
    private Integer parentId;
}
