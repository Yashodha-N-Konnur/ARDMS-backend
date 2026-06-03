package com.ardms.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date/time formatting operations.
 */
public final class DateTimeUtils {

    private static final DateTimeFormatter DEFAULT_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateTimeUtils() {}

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DEFAULT_FORMATTER);
    }

    public static String formatForLog(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static long durationInSeconds(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0;
        return java.time.Duration.between(start, end).getSeconds();
    }
}
