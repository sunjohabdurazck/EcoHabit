package main.java.com.ecohabit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.com.ecohabit.model.User;

public class UserDAO {
    private User currentUser;
    
    public UserDAO() {
        // Initialize with a sample user
        currentUser = new User("John", "Doe", "john.doe@example.com");
        currentUser.setCurrentStreak(15);
        currentUser.setTotalCO2Saved(247.5);
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void updateUser(User user) {
        this.currentUser = user;
    }
    
    public User getUserByEmailAndPassword(String email, String password) {
        String query = "SELECT id, first_name, last_name, email, current_streak " +
                       "FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password); // 🔒 ideally hash & compare

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setCurrentStreak(rs.getInt("current_streak"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
}
