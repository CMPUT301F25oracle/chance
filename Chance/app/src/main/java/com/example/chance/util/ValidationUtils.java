package com.example.chance.util;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Helper for validating user input fields.
 */
public class ValidationUtils {

    /**
     * Returns true if a string is null or empty.
     */
    public static boolean isEmpty(String input) {
        return TextUtils.isEmpty(input);
    }

    /**
     * Validates email format.
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validates phone number (basic pattern).
     */
    public static boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches();
    }

    /**
     * Ensures minimum password length.
     */
    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    /**
     * Validates numeric-only input.
     */
    public static boolean isNumeric(String input) {
        return input != null && input.matches("\\d+");
    }
}
