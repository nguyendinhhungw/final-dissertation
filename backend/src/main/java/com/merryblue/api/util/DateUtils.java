package com.merryblue.api.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String format(OffsetDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

    public static LocalDateTime toLocalDateTime(OffsetDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    private DateUtils() {}
}
