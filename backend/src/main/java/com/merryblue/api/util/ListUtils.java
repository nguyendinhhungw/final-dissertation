package com.merryblue.api.util;

import java.util.Collection;

public class ListUtils {
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    
    private ListUtils() {}
}
