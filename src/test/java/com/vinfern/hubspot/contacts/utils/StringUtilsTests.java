package com.vinfern.hubspot.contacts.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringUtilsTests {

    @Test
    public void testIsNullOrEmpty_withNull() {
        assertTrue(StringUtils.isNullOrEmpty(null), "Expected true for null input");
    }

    @Test
    public void testIsNullOrEmpty_withEmptyString() {
        assertTrue(StringUtils.isNullOrEmpty(""), "Expected true for empty string input");
    }

    @Test
    public void testIsNullOrEmpty_withWhitespaceString() {
        assertTrue(StringUtils.isNullOrEmpty("   "), "Expected true for string with only whitespace");
    }

    @Test
    public void testIsNullOrEmpty_withNonEmptyString() {
        assertFalse(StringUtils.isNullOrEmpty("Hello"), "Expected false for non-empty string input");
    }

    @Test
    public void testIsNullOrEmpty_withStringContainingSpaces() {
        assertFalse(StringUtils.isNullOrEmpty("  Hello  "), "Expected false for string with spaces and text");
    }
}
