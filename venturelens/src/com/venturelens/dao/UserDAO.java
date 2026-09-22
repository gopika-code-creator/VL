package com.venturelens.dao;

import com.venturelens.model.User;
import com.venturelens.utils.SecurityUtil;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data Access Object for User entities.
 * Strictly uses official JDBC (java.sql.*) and try-with-resources.
 */
public class UserDAO {

    // Fallback store for offline demo mode if MySQL service is not yet started locally
    private static final Map<String, User> IN_MEMORY_USERS = new ConcurrentHashMap<>();

    static {
        // Pre-seed default demo founder (password: admin123)
        User demo = new User(1, "founder_alex", "alex@venturelens.internal",
                SecurityUtil.hashPassword("admin123"), new Timestamp(System.currentTimeMillis()));
        IN_MEMORY_USERS.put("founder_alex", demo);
    }

    /**
     * Authenticates user with username and plain password using SHA-256 hash comparison.
     */
    public User authenticate(String username, String plainPassword) throws SQLException {
        String hash = SecurityUtil.hashPassword(plainPassword);

        String sql = "SELECT id, username, email, password_hash, created_at FROM users WHERE username = ? AND password_hash = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hash);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            // Check fallback in-memory store if DB is offline
            User mock = IN_MEMORY_USERS.get(username);
            if (mock != null && SecurityUtil.verifyPassword(plainPassword, mock.getPasswordHash())) {
                return mock;
            }
            throw e;
        }

        return null;
    }

    /**
     * Registers a new user with SHA-256 hashed password.
     */
    public User register(String username, String email, String plainPassword) throws SQLException {
        String hash = SecurityUtil.hashPassword(plainPassword);

        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hash);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    User newUser = new User(id, username, email, hash, new Timestamp(System.currentTimeMillis()));
                    IN_MEMORY_USERS.put(username, newUser);
                    return newUser;
                }
            }
        } catch (SQLException e) {
            // Check fallback for offline demo testing
            int newId = IN_MEMORY_USERS.size() + 1;
            User fallback = new User(newId, username, email, hash, new Timestamp(System.currentTimeMillis()));
            IN_MEMORY_USERS.put(username, fallback);
            return fallback;
        }

        throw new SQLException("Failed to retrieve generated key for user");
    }
}
