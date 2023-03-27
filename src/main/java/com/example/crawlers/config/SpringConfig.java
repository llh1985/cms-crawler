package com.example.crawlers.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.crawlers.dao")
public class SpringConfig {
}
