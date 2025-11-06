package com.example.chance.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Utility for formatting and comparing dates.
 */
public class DateUtils {

    /**
     * Returns a formatted date string like "Nov 6, 2025".
     */
    public static String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Returns relative time text like "3 hours ago", "Yesterday", or "2 days ago".
     */
    public static String getRelativeTime(Date date) {
        if (date == null) return "";

        long diffMillis = System.currentTimeMillis() - date.getTime();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(diffMillis);
        long days = TimeUnit.MILLISECONDS.toDays(diffMillis);

        if (minutes < 1) return "Just now";
        else if (minutes < 60) return minutes + " minutes ago";
        else if (hours < 24) return hours + " hours ago";
        else if (days == 1) return "Yesterday";
        else return days + " days ago";
    }

    /**
     * Compares if a date is in the past.
     */
    public static boolean isPast(Date date) {
        return date != null && date.before(new Date());
    }

    /**
     * Compares if a date is today.
     */
    public static boolean isToday(Date date) {
        if (date == null) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(date).equals(sdf.format(new Date()));
    }
}
