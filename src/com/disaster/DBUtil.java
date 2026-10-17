package com.disaster;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBUtil - Database Utility Class
 * Manages PostgreSQL JDBC connections for the Disaster Safety Game.
 * 
 * Uses PostgreSQL with:
 *   - Database: disaster_safety
 *   - Username: postgres
 *   - Password: harini10
 */
public class DBUtil {

    // ---- Database Configuration ----
    private static final String DB_URL  = "jdbc:postgresql://localhost:5432/disaster_safety";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "harini10";

    /**
     * Returns a new database connection.
     * Make sure to close this connection after use!
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // Load the PostgreSQL JDBC driver
        Class.forName("org.postgresql.Driver");
        // Create and return a connection
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
