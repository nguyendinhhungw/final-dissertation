package com.merryblue.api.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringNormalizationUtils {

    public static String toSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("").toLowerCase();
        slug = slug.replaceAll("[^a-z0-9\\s]", "");
        slug = slug.replaceAll("\\s+", "-");
        return slug;
    }

    private StringNormalizationUtils() {}
}
