package main.java.com.ecohabit.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private static final String DB_URL = "jdbc:sqlite:ecohabit.db";
    private static DBManager instance;
    private Connection connection;

    private DBManager() {
        initializeDatabase();
    }

    public static synchronized DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    public void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    private void createTables() {
        String[] createTableQueries = {
            // Users table
            "CREATE TABLE IF NOT EXISTS users (" +
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
            "user_type TEXT, " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "last_login DATETIME)",

            // Activities table
            "CREATE TABLE IF NOT EXISTS activities (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER, " +
            "activity_type TEXT NOT NULL, " +
            "category TEXT NOT NULL, " +
            "quantity REAL NOT NULL, " +
            "unit TEXT NOT NULL, " +
            "co2_saved REAL NOT NULL, " +
            "custom_activity TEXT, " +
            "notes TEXT, " +
            "activity_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (user_id) REFERENCES users (id))",

            // Settings table
            "CREATE TABLE IF NOT EXISTS user_settings (" +
            "user_id INTEGER PRIMARY KEY, " +
            "theme TEXT DEFAULT 'dark', " +
            "font_size INTEGER DEFAULT 14, " +
            "high_contrast BOOLEAN DEFAULT FALSE, " +
            "daily_reminders BOOLEAN DEFAULT TRUE, " +
            "weekly_summaries BOOLEAN DEFAULT TRUE, " +
            "achievement_notifications BOOLEAN DEFAULT TRUE, " +
            "eco_tips BOOLEAN DEFAULT TRUE, " +
            "notification_sound TEXT DEFAULT 'default', " +
            "data_collection BOOLEAN DEFAULT TRUE, " +
            "personalized_ads BOOLEAN DEFAULT FALSE, " +
            "FOREIGN KEY (user_id) REFERENCES users (id))",

            // Achievements table
            "CREATE TABLE IF NOT EXISTS achievements (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER, " +
            "name TEXT NOT NULL, " +
            "description TEXT, " +
            "icon TEXT, " +
            "earned_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (user_id) REFERENCES users (id))",

            // Eco tips table
            "CREATE TABLE IF NOT EXISTS eco_tips (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "tip_text TEXT NOT NULL, " +
            "category TEXT, " +
            "difficulty TEXT, " +
            "co2_impact REAL)"
        };

        try (Statement stmt = connection.createStatement()) {
            for (String query : createTableQueries) {
                stmt.execute(query);
            }
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public int executeUpdate(String query, Object... params) throws SQLException {
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            return pstmt.executeUpdate();
        }
    }

    public ResultSet executeQuery(String query, Object... params) throws SQLException {
        PreparedStatement pstmt = getConnection().prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
        return pstmt.executeQuery();
    }

    public List<String[]> executeQueryAsList(String query, Object... params) throws SQLException {
        List<String[]> results = new ArrayList<>();
        try (ResultSet rs = executeQuery(query, params)) {
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getString(i + 1);
                }
                results.add(row);
            }
        }
        return results;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

	public void closeConnection() {
		// TODO Auto-generated method stub
		
	}
}
