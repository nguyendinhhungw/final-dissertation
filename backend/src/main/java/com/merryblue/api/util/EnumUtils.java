package com.merryblue.api.util;

public class EnumUtils {
    public static <E extends Enum<E>> E fromString(Class<E> enumClass, String value) {
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
    
    private EnumUtils() {}
}
