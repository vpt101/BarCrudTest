package com.brclys.thct.delegate;

public class Util {
    private static String generateRandomNumber(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }

    public static String generateAccountNumber() {
        // Generate 8-digit account number
        return generateRandomNumber(8);
    }

    public static String generateSortCode() {
        // Generate sort code in format XX-XX-XX
        return String.format("%s-%s-%s",
                generateRandomNumber(2),
                generateRandomNumber(2),
                generateRandomNumber(2));
    }

}
