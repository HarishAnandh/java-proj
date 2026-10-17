package com.satark.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBUtil - JDBC Database Utility Class for SATARK.
 * Provides direct connection to PostgreSQL database disaster_safety.
 */
public class DBUtil {

    private static final String DB_URL  = "jdbc:postgresql://localhost:5432/disaster_safety";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "harini10";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[SATARK] PostgreSQL Driver not found: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
