package com.merryblue.api.util;

public class UrlUtils {
    public static boolean isValidUrl(String url) {
        return url != null && url.startsWith("http");
    }
    private UrlUtils() {}
}
