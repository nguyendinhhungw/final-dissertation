package com.merryblue.api.util;

import java.util.Collection;

public class CollectionUtils {
    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }
    private CollectionUtils() {}
}
