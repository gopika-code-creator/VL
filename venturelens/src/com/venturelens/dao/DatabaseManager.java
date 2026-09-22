package com.venturelens.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages official JDBC connections to MySQL database with try-with-resources compliance.
 * Default connection strings match production configuration:
 * JDBC URL: jdbc:mysql://localhost:3306/venturelens_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
 */
public class DatabaseManager {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/venturelens_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "password";

    private static String jdbcUrl = DEFAULT_URL;
    private static String username = DEFAULT_USER;
    private static String password = DEFAULT_PASS;

    private static volatile boolean mockModeEnabled = false;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Driver not loaded yet or in container test
        }
    }

    public static synchronized void configure(String url, String user, String pass) {
        jdbcUrl = url;
        username = user;
        password = pass;
    }

    public static synchronized void setMockMode(boolean mock) {
        mockModeEnabled = mock;
    }

    public static synchronized boolean isMockMode() {
        return mockModeEnabled;
    }

    /**
     * Obtains a new database connection.
     * All callers MUST wrap connections in try-with-resources blocks.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    /**
     * Tests connectivity to the database.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
