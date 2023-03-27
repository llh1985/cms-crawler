package com.example.crawlers.utils;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Component
public class FileUtils {

    @Value("${download.path}")
    private String downloadPath;

    /**
     * 根据url下载文件到本地指定目录
     *
     * @param urlString 文件url
     * @return 文件名
     * @throws IOException IO异常
     */
    public String downloadFile(String urlString) throws IOException {
        URL url = new URL(urlString);
        InputStream inputStream = url.openStream();
        byte[] bytes = IOUtils.toByteArray(inputStream);
        inputStream.close();

        // 获取文件名
        String fileName = urlString.substring(urlString.lastIndexOf("/") + 1);
        // 获取文件目录
        String dirPath = downloadPath + File.separator + CrawlerUtils.getDirFromUrl(urlString);
        // 创建文件夹
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 创建文件
        File file = new File(dir, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        // 写入文件
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(bytes);
        outputStream.close();

        return file.getName();
    }

    /**
     * 将字符串写入到指定目录下的文件中
     *
     * @param content    字符串内容
     * @param targetPath 目标文件路径
     * @return 文件名
     * @throws IOException IO异常
     */
    public String saveToFile(String content, String targetPath) throws IOException {
        // 获取文件名
        String fileName = UUID.randomUUID().toString() + ".html";
        // 获取文件目录
        String dirPath = downloadPath + File.separator + targetPath;
        // 创建文件夹
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 创建文件
        File file = new File(dir, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        // 写入文件
        FileOutputStream outputStream = new FileOutputStream(file);
        IOUtils.write(content, outputStream, "UTF-8");
        outputStream.close();

        return file.getName();
    }
}
