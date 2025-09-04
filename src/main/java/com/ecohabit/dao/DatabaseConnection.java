package main.java.com.ecohabit.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    // SQLite database file path
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/db/app.db";

    // Private constructor to enforce singleton pattern (optional)
    private DatabaseConnection() {}

    /**
     * Get a database connection
     * @return Connection object
     */
    
    private static DatabaseConnection instance;

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            // Establish connection
            connection = DriverManager.getConnection(DB_URL);
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database!");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Initialize the database and create necessary tables
     */
    public static void initializeDatabase() {
        String dropTableSQL = "DROP TABLE IF EXISTS users;";
        String createUsersTableSQL = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "first_name TEXT, " +
                "last_name TEXT, " +
                "age INTEGER, " +
                "gender TEXT, " +
                "location TEXT, " +
                "diet_preference TEXT, " +
                "transport_preference TEXT, " +
                "current_streak INTEGER DEFAULT 0, " +
                "co2_saved REAL DEFAULT 0, " +
                "total_co2_saved REAL DEFAULT 0, " +
                "user_type TEXT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "last_login DATETIME" +
                ");";
       
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTableSQL);
            System.out.println("Users table is ready.");
        } catch (SQLException e) {
            System.err.println("Failed to create users table.");
            e.printStackTrace();
        }
    }

    /**
     * Close a database connection safely
     * @param connection Connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
