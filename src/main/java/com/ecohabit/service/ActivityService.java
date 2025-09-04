package main.java.com.ecohabit.service;

import main.java.com.ecohabit.model.Activity;
import main.java.com.ecohabit.dao.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service class for handling Activity-related database operations
 */
public class ActivityService {
    
    private final DatabaseConnection dbConnection;
    
    public ActivityService() {
        this.dbConnection = DatabaseConnection.getInstance();
        initializeDatabase(); // Initialize database tables
    }
    
    /**
     * Initialize database tables
     */
    private void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS activities (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "description TEXT NOT NULL, " +
            "activity_date TEXT NOT NULL, " +
            "category TEXT NOT NULL, " +
            "co2_saved REAL NOT NULL, " +
            "quantity REAL NOT NULL, " +
            "unit TEXT NOT NULL, " +
            "notes TEXT, " +
            "completed INTEGER DEFAULT 1, " +
            "created_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TEXT, " +
            "FOREIGN KEY (user_id) REFERENCES users(id)" +
            ")";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createTableSQL);
            System.out.println("Activities table created or verified successfully");
            
        } catch (SQLException e) {
            System.err.println("Error creating activities table: " + e.getMessage());
            // If foreign key constraint fails, try without it
            createTableWithoutForeignKey();
        }
    }
    
    /**
     * Create table without foreign key constraint (fallback)
     */
    private void createTableWithoutForeignKey() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS activities (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "description TEXT NOT NULL, " +
            "activity_date TEXT NOT NULL, " +
            "category TEXT NOT NULL, " +
            "co2_saved REAL NOT NULL, " +
            "quantity REAL NOT NULL, " +
            "unit TEXT NOT NULL, " +
            "notes TEXT, " +
            "completed INTEGER DEFAULT 1, " +
            "created_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TEXT" +
            ")";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createTableSQL);
            System.out.println("Activities table created without foreign key constraint");
            
        } catch (SQLException e) {
            System.err.println("Error creating activities table (fallback): " + e.getMessage());
        }
    }
    
    /**
     * Get all activities for a specific user
     */
    public List<Activity> getUserActivities(String userId) {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM activities WHERE user_id = ? ORDER BY activity_date DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, Integer.parseInt(userId));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching user activities: " + e.getMessage());
            // For demo purposes, return empty list instead of throwing
        } catch (NumberFormatException e) {
            System.err.println("Invalid user ID format: " + userId);
        }
        
        return activities;
    }
    
    /**
     * Save a new activity for a user
     */
    public boolean saveActivity(String userId, Activity activity) {
        // First, ensure the table exists
        if (!tableExists("activities")) {
            initializeDatabase();
        }
        
        String sql = "INSERT INTO activities (user_id, description, activity_date, category, co2_saved, quantity, unit, notes, completed) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, Integer.parseInt(userId));
            pstmt.setString(2, activity.getDescription());
            pstmt.setString(3, activity.getDate().toString());
            pstmt.setString(4, activity.getCategory());
            pstmt.setDouble(5, activity.getCo2Saved());
            pstmt.setDouble(6, activity.getQuantity());
            pstmt.setString(7, activity.getUnit());
            pstmt.setString(8, activity.getNotes());
            pstmt.setBoolean(9, activity.isCompleted());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving activity: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (NumberFormatException e) {
            System.err.println("Invalid user ID format: " + userId);
            return false;
        }
    }
    
    /**
     * Check if a table exists in the database
     */
    private boolean tableExists(String tableName) {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tableName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            System.err.println("Error checking if table exists: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete an activity
     */
    public boolean deleteActivity(String userId, String activityId) {
        String sql = "DELETE FROM activities WHERE id = ? AND user_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, Integer.parseInt(activityId));
            pstmt.setInt(2, Integer.parseInt(userId));
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting activity: " + e.getMessage());
            return false;
        } catch (NumberFormatException e) {
            System.err.println("Invalid ID format - userId: " + userId + ", activityId: " + activityId);
            return false;
        }
    }
    
    /**
     * Helper method to map ResultSet to Activity object
     */
    private Activity mapResultSetToActivity(ResultSet rs) throws SQLException {
        Activity activity = new Activity();
        activity.setId(rs.getInt("id"));
        activity.setDescription(rs.getString("description"));
        
        // Handle date conversion safely
        String dateStr = rs.getString("activity_date");
        if (dateStr != null) {
            try {
                activity.setDate(LocalDate.parse(dateStr));
            } catch (Exception e) {
                System.err.println("Error parsing date: " + dateStr);
                activity.setDate(LocalDate.now());
            }
        } else {
            activity.setDate(LocalDate.now());
        }
        
        activity.setCategory(rs.getString("category"));
        activity.setCo2Saved(rs.getDouble("co2_saved"));
        activity.setQuantity(rs.getDouble("quantity"));
        activity.setUnit(rs.getString("unit"));
        activity.setNotes(rs.getString("notes"));
        activity.setCompleted(rs.getBoolean("completed"));
        
        return activity;
    }
    
    // ... keep the rest of your methods, but add similar error handling ...
    
    /**
     * Get total CO2 saved by a user
     */
    public double getTotalCO2Saved(String userId) {
        if (!tableExists("activities")) {
            return 0.0;
        }
        
        String sql = "SELECT SUM(co2_saved) as total_co2 FROM activities WHERE user_id = ? AND completed = 1";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, Integer.parseInt(userId));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total_co2");
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating total CO2 saved: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid user ID format: " + userId);
        }
        
        return 0.0;
    }
}