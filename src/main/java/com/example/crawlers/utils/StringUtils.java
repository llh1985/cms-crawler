package com.example.crawlers.utils;

import org.jsoup.nodes.Element;

public class StringUtils {
    public static String trim(String str) {
        return str == null ? "" : str.trim();
    }

    public static String getAttributeValue(Element element, String attrName) {
        String value = element.attr(attrName);
        return trim(value);
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
