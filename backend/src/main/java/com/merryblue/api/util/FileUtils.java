package com.merryblue.api.util;

import java.util.UUID;

public class FileUtils {

    public static String getExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static String generateUniqueFileName(String originalFileName) {
        return UUID.randomUUID().toString() + getExtension(originalFileName);
    }

    private FileUtils() {
    }
}
