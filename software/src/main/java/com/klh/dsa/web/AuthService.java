package com.klh.dsa.web;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Holds user credentials and session tokens for the web UI. */
public class AuthService {
    public static final class User {
        private final String username;
        private final String passwordHash;
        private final String role;

        public User(String username, String passwordHash, String role) {
            this.username = username == null ? "" : username.trim();
            this.passwordHash = passwordHash == null ? "" : passwordHash;
            this.role = role == null ? "USER" : role.trim().toUpperCase(Locale.ROOT);
        }

        public String getUsername() {
            return username;
        }

        public String getPasswordHash() {
            return passwordHash;
        }

        public String getRole() {
            return role;
        }
    }

    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> sessions = new ConcurrentHashMap<>();

    public AuthService() {
        String adminHash = hashPassword("admin@123");
        users.put("admin", new User("admin", adminHash, "ADMIN"));
    }

    public boolean register(String username, String password, String role) {
        if (username == null || password == null) {
            return false;
        }
        String trimmedUsername = username.trim();
        String trimmedPassword = password.trim();
        if (trimmedUsername.isEmpty() || trimmedPassword.isEmpty()) {
            return false;
        }
        if (userExists(trimmedUsername)) {
            return false;
        }
        if (trimmedUsername.contains(" ")) {
            return false;
        }
        User user = new User(trimmedUsername, hashPassword(trimmedPassword), normalizeRole(role));
        users.put(trimmedUsername, user);
        return true;
    }

    public String login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        User user = users.get(username.trim());
        if (user == null) {
            return null;
        }
        if (!constantTimeEquals(user.getPasswordHash(), hashPassword(password))) {
            return null;
        }
        String token = UUID.randomUUID().toString();
        sessions.put(token, user.getUsername());
        return token;
    }

    public String validate(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return sessions.get(token);
    }

    public void logout(String token) {
        if (token != null) {
            sessions.remove(token);
        }
    }

    public String getRole(String username) {
        if (username == null) {
            return "USER";
        }
        User user = users.get(username.trim());
        return user == null ? "USER" : user.getRole();
    }

    public boolean userExists(String username) {
        if (username == null) {
            return false;
        }
        return users.containsKey(username.trim());
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte value : hash) {
                builder.append(String.format("%02x", value));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            return Integer.toHexString(password.hashCode());
        }
    }

    private static boolean constantTimeEquals(String left, String right) {
        if (left == null || right == null) {
            return left == null && right == null;
        }
        int one = left.length();
        int two = right.length();
        int limit = Math.max(one, two);
        int result = 0;
        for (int i = 0; i < limit; i++) {
            char a = i < one ? left.charAt(i) : 0;
            char b = i < two ? right.charAt(i) : 0;
            result |= a ^ b;
        }
        return result == 0;
    }

    private static String normalizeRole(String role) {
        if (role == null) {
            return "USER";
        }
        String normalized = role.trim().toUpperCase(Locale.ROOT);
        return "ADMIN".equals(normalized) ? "ADMIN" : "USER";
    }
}
