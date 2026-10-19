package com.blood_donor.utils;

public final class SqlUtils {

    private SqlUtils() {}

    /**
     * Escapes SQL LIKE wildcard characters (%, _, \)
     * so that user input can be safely matched literally in queries using ESCAPE '\'.
     */
    public static String escapeLikeWildcards(String input) {
        if (input == null) {
            return null;
        }
        return input
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
