package com.merryblue.api.util;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {
    public static String formatVnd(long amount) {
        NumberFormat vnFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return vnFormat.format(amount);
    }
    private CurrencyUtils() {}
}
