package com.example.workforcemanagement.util;

import java.util.regex.Pattern;

public class PasswordValidator {
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[@$!%*?&]");

    public static String validatePassword(String password) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            return "Password must contain at least one lowercase letter.";
        }
        if (!NUMBER_PATTERN.matcher(password).find()) {
            return "Password must contain at least one number.";
        }
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            return "Password must contain at least one special character (@$!%*?&).";
        }
        return null; // Null indicates valid password
    }
}