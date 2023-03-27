package com.example.crawlers.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class DownloadConfig {
    @Value("${download.path}")
    private String downloadPath;

    @Bean
    public File downloadDir() {
        File dir = new File(downloadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
