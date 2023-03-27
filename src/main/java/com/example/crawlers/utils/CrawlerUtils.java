package com.example.crawlers.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CrawlerUtils {
//    public static String getFileName(String url) {
//        String[] segments = url.split("/");
//        return segments[segments.length - 1];
//    }
//
//    public static void downloadFile(String url, File dir) throws IOException {
//        String fileName = getFileName(url);
//        File file = new File(dir, fileName);
//        FileUtils.copyURLToFile(new URL(url), file);
//    }
//
//    public static String replaceDomain(String content, String oldDomain, String newDomain) {
//        return content.replaceAll(oldDomain, newDomain);
//    }
    public static String getDirFromUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }

        String host = null;
        try {
            host = new URL(url).getHost();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String path = null;
        try {
            path = new URL(url).getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String ext = FilenameUtils.getExtension(path);
        if (StringUtils.isBlank(ext)) {
            return host + path.replaceAll("/", File.separator);
        } else {
            return host + path.replaceAll("/", File.separator).replace("." + ext, "") + File.separator + ext;
        }
    }

    public static void saveToFile(String filePath, String content) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            IOUtils.write(content, fos, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    public static void download(String urlStr, String dirPath) throws IOException {
        if (StringUtils.isBlank(urlStr) || StringUtils.isBlank(dirPath)) {
            return;
        }

        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            URL url = new URL(urlStr);
            inputStream = url.openStream();
            byte[] buffer = new byte[1024];
            int length;

            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = FilenameUtils.getName(url.getPath());
            File file = new File(dirPath + File.separator + fileName);
            outputStream = new FileOutputStream(file);

            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }
}
