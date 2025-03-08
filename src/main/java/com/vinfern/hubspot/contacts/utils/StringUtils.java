package com.vinfern.hubspot.contacts.utils;

public class StringUtils {
    private StringUtils() {
    }
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
