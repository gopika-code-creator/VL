package com.venturelens.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Standard cryptographic hashing utilities using standard JDK java.security.MessageDigest.
 */
public final class SecurityUtil {
    private SecurityUtil() {}

    /**
     * Hashes an input password string into a 64-character lowercase SHA-256 hexadecimal string.
     */
    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm unavailable in current JVM", e);
        }
    }

    /**
     * Validates whether a raw plain password matches an expected SHA-256 hash.
     */
    public static boolean verifyPassword(String rawPassword, String expectedHash) {
        if (rawPassword == null || expectedHash == null) {
            return false;
        }
        String calculated = hashPassword(rawPassword);
        return calculated.equalsIgnoreCase(expectedHash);
    }
}
