package com.example.cailanzi.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {
    private static final String SECRET_KEY = "cailanzi-secret-key-2024";
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24小时

    public static String generateToken(String username) {
        long expiration = System.currentTimeMillis() + EXPIRATION_TIME;
        String payload = username + ":" + expiration + ":" + UUID.randomUUID();
        return Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    public static String getUsernameFromToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":");
            if (parts.length >= 1) {
                return parts[0];
            }
        } catch (Exception e) {
            // Token无效
        }
        return null;
    }

    public static boolean validateToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":");
            if (parts.length >= 2) {
                long expiration = Long.parseLong(parts[1]);
                return System.currentTimeMillis() < expiration;
            }
        } catch (Exception e) {
            // Token无效
        }
        return false;
    }
}

