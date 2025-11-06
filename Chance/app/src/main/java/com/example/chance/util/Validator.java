package com.example.chance.util;

/**
 * Additional validation utilities (extends ValidationUtils).
 */
public class Validator {

    /**
     * Validates event capacity is positive.
     */
    public static boolean isValidCapacity(int capacity) {
        return capacity > 0;
    }

    /**
     * Validates price is non-negative.
     */
    public static boolean isValidPrice(double price) {
        return price >= 0;
    }
}