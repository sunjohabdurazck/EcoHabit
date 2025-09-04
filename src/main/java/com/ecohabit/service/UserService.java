package main.java.com.ecohabit.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.com.ecohabit.dao.DatabaseConnection;
import main.java.com.ecohabit.model.User;

public class UserService {
    private static UserService instance;
    private DatabaseService databaseService;
    
    public UserService() {
        this.databaseService = DatabaseService.getInstance();
    }
    
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
    
    public boolean updateUser(User user) {
        return databaseService.updateUser(user);
    }
    
    public User getUserById(int id) {
        // Implementation to get user by ID
        return null; // Placeholder
    }
    
    public boolean deleteUser(int userId) {
        // Implementation to delete user
        return false; // Placeholder
    }
    public boolean setUserLoggedIn(int userId, boolean isLoggedIn) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE users SET is_logged_in = ? WHERE id = ?")) {
            
            stmt.setBoolean(1, isLoggedIn);
            stmt.setInt(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loginUser(String email, String password) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM users WHERE email = ? AND password = ?")) {
            
            stmt.setString(1, email);
            stmt.setString(2, password); // Note: In production, use hashed passwords
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("id");
                // First, log out any currently logged-in user
                logOutAllUsers();
                // Then log in this user
                return setUserLoggedIn(userId, true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean logOutAllUsers() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE users SET is_logged_in = 0")) {
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected >= 0; // Could be 0 if no users were logged in
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
   
    public User getCurrentUser() {
        // Retrieve the currently logged-in user (where is_logged_in = true)
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE is_logged_in = 1 LIMIT 1")) {
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setHashedPassword(rs.getString("password"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setAge(rs.getInt("age"));
                user.setGender(rs.getString("gender"));
                user.setDietPreference(rs.getString("diet_preference")); // Fixed: should be diet_preference
                user.setTransportPreference(rs.getString("transport_preference"));
                user.setUserType(rs.getString("user_type"));
                user.setCurrentStreak(rs.getInt("current_streak"));
                user.setTotalCO2Saved(rs.getDouble("total_co2_saved"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	  public boolean doesEmailExist(String email) {
	        // Check if email exists in database
	        try (Connection conn = DatabaseConnection.getConnection();
	             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM users WHERE email = ?")) {
	            stmt.setString(1, email);
	            ResultSet rs = stmt.executeQuery();
	            return rs.next();
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	    
	    public boolean createUser(User user) {
	        // Insert user into database
	        try (Connection conn = DatabaseConnection.getConnection();
	             PreparedStatement stmt = conn.prepareStatement(
	                 "INSERT INTO users (email, password, first_name, last_name, age, gender, diet_preference, transport_preference, user_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
	            
	            stmt.setString(1, user.getEmail());
	            stmt.setString(2, user.getHashedPassword()); // This should be hashed
	            stmt.setString(3, user.getFirstName());
	            stmt.setString(4, user.getLastName());
	            stmt.setInt(5, user.getAge());
	            stmt.setString(6, user.getGender());
	            stmt.setString(7, user.getTransportPreference());
	            stmt.setString(8, user.getTransportPreference());
	            stmt.setString(9, user.getUserType());
	            
	            
	            
	            
	            int rowsAffected = stmt.executeUpdate();
	            return rowsAffected > 0;
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	    
	    public static User getUserByEmail(String email) {
	        try (Connection conn = DatabaseConnection.getConnection();
	             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE email = ?")) {
	            stmt.setString(1, email);
	            ResultSet rs = stmt.executeQuery();
	            
	            if (rs.next()) {
	                User user = new User();
	                user.setId(rs.getInt("id"));
	                user.setEmail(rs.getString("email"));
	                user.setHashedPassword(rs.getString("password"));
	                user.setFirstName(rs.getString("first_name"));
	                user.setLastName(rs.getString("last_name"));
	                user.setAge(rs.getInt("age"));
	                user.setGender(rs.getString("gender"));
	                user.setDietPreference(rs.getString("diet_preference")); // FIXED: diet_preference
	                user.setTransportPreference(rs.getString("transport_preference"));
	                user.setUserType(rs.getString("user_type"));
	                user.setCurrentStreak(rs.getInt("current_streak"));
	                user.setTotalCO2Saved(rs.getDouble("total_co2_saved"));
	                return user;
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
}

