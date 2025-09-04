package main.java.com.ecohabit.service;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import main.java.com.ecohabit.model.User;
import main.java.com.ecohabit.util.PasswordUtil;
import java.util.Base64;

public class DatabaseService {
    private static DatabaseService instance;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/db/app.db";
    
    private DatabaseService() {}
    
    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }
    
    
    public void initialize() throws SQLException {
        // ✅ Create the directory if it doesn't exist
        File dbDir = new File("src/main/resources/db");
        if (!dbDir.exists()) {
            boolean created = dbDir.mkdirs();
            System.out.println("Created db directory: " + created);
        }
        
        // ✅ Check if database file exists
        File dbFile = new File("src/main/resources/db/app.db");
        System.out.println("Database file exists: " + dbFile.exists());
        System.out.println("Database file path: " + dbFile.getAbsolutePath());
        
        connection = DriverManager.getConnection(DB_URL);
        createTables();
        System.out.println("Database initialized successfully with app.db");
        hashExistingPasswords();
        
        // ✅ Add debug to see what's in the database
        debugDatabaseContents();
    }
    
    public boolean isInitialized() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    private void createTables() throws SQLException {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,  -- ✅ Changed from hashed_password to password
                first_name TEXT,
                last_name TEXT,
                age INTEGER,
                gender TEXT,
                location TEXT,
                diet_preference TEXT,    -- ✅ Changed from diet_type
                transport_preference TEXT, -- ✅ Changed from transport_method
                user_type TEXT,
                created_at TEXT NOT NULL,
                last_login TEXT,
                current_streak INTEGER DEFAULT 0,
                co2_saved REAL DEFAULT 0.0,  -- ✅ This column exists in your DB
                total_co2_saved REAL DEFAULT 0.0
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
        }
    }
    
    // ✅ ADD DEBUG METHOD: Check database contents
    public void debugDatabaseContents() {
        try {
            System.out.println("=== DATABASE DEBUG INFO ===");
            
            // Check what tables exist
            Statement stmt = connection.createStatement();
            ResultSet tables = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");
            
            System.out.println("Tables in database:");
            boolean usersTableExists = false;
            while (tables.next()) {
                String tableName = tables.getString("name");
                System.out.println("  - " + tableName);
                
                if ("users".equals(tableName)) {
                    usersTableExists = true;
                }
                
                // Count rows in each table
                if (!tableName.startsWith("sqlite_")) {
                    ResultSet countRs = stmt.executeQuery("SELECT COUNT(*) as count FROM " + tableName);
                    if (countRs.next()) {
                        System.out.println("    Rows: " + countRs.getInt("count"));
                    }
                }
            }
            
            // Check if users table exists and has data
            System.out.println("\n=== USERS TABLE DETAILS ===");
            if (usersTableExists) {
                ResultSet userData = stmt.executeQuery("SELECT * FROM users");
                ResultSetMetaData metaData = userData.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                System.out.println("Users table columns:");
                for (int i = 1; i <= columnCount; i++) {
                    System.out.println("  - " + metaData.getColumnName(i));
                }
                
                // Show first few users
                System.out.println("\nUsers in database:");
                int count = 0;
                while (userData.next() && count < 10) {
                    System.out.println("User " + (count + 1) + ":");
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.println("  " + metaData.getColumnName(i) + ": " + userData.getString(i));
                    }
                    count++;
                    System.out.println();
                }
                
                if (count == 0) {
                    System.out.println("  (No users in database)");
                }
            } else {
                System.out.println("Users table does not exist!");
            }
            
        } catch (SQLException e) {
            System.err.println("Error debugging database: " + e.getMessage());
        }
    }
    
    // ✅ ADD DEBUG METHOD: List all user emails
    private void debugAllUserEmails() {
        try {
            System.out.println("All emails in database:");
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT email FROM users");
            
            int count = 0;
            while (rs.next()) {
                System.out.println("  - '" + rs.getString("email") + "'");
                count++;
            }
            
            if (count == 0) {
                System.out.println("  (No users in database)");
            }
            
        } catch (SQLException e) {
            System.err.println("Error listing emails: " + e.getMessage());
        }
    }
    
    public boolean saveUser(User user) {
        // ✅ Update the SQL to match your database column names
        String sql = """
            INSERT INTO users (email, password, first_name, last_name, age, gender,
                             diet_preference, transport_preference, user_type, location, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { 
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getHashedPassword());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setObject(5, user.getAge() == 0 ? null : user.getAge());
            pstmt.setString(6, user.getGender());
            
            // ✅ Use the correct column names
            pstmt.setString(7, user.getDietType());
            pstmt.setString(8, user.getTransportationMethod());
            pstmt.setString(9, user.getUserType());
            pstmt.setString(10, user.getLocation());
            pstmt.setString(11, user.getCreatedAt().toString());
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public User getUserByEmail(String email) {
        System.out.println("=== getUserByEmail DEBUG ===");
        System.out.println("Searching for email: '" + email + "'");
        
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("✅ FOUND USER: " + rs.getString("email"));
                User user = createUserFromResultSet(rs);
                System.out.println("User details: " + user.getFirstName() + " " + user.getLastName());
                return user;
            } else {
                System.out.println("❌ NO USER FOUND with email: " + email);
                // Let's see what emails actually exist
                debugAllUserEmails();
            }
        } catch (SQLException e) {
            System.err.println("Error in getUserByEmail: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updateUser(User user) {
        // ✅ Update the SQL to match your database column names
        String sql = """
            UPDATE users SET first_name = ?, last_name = ?, age = ?, gender = ?,
                           diet_preference = ?, transport_preference = ?, user_type = ?, location = ?,
                           last_login = ?, current_streak = ?, total_co2_saved = ?
            WHERE id = ?
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setObject(3, user.getAge() == 0 ? null : user.getAge());
            pstmt.setString(4, user.getGender());
            
            // ✅ Use the correct column names
            pstmt.setString(5, user.getDietType());
            pstmt.setString(6, user.getTransportationMethod());
            pstmt.setString(7, user.getUserType());
            pstmt.setString(8, user.getLocation());
            pstmt.setString(9, user.getLastLogin() != null ? user.getLastLogin().toString() : null);
            pstmt.setInt(10, user.getCurrentStreak());
            pstmt.setDouble(11, user.getTotalCO2Saved());
            pstmt.setInt(12, user.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setHashedPassword(rs.getString("password"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setAge(rs.getInt("age"));
        user.setGender(rs.getString("gender"));
        user.setDietType(rs.getString("diet_preference"));
        user.setTransportationMethod(rs.getString("transport_preference"));
        user.setUserType(rs.getString("user_type"));
        user.setLocation(rs.getString("location"));
        
        // ✅ FIX DATE PARSING: Handle the SQLite datetime format
        String createdAt = rs.getString("created_at");
        if (createdAt != null) {
            try {
                // Replace space with 'T' to match ISO format
                String isoFormat = createdAt.replace(' ', 'T');
                user.setCreatedAt(LocalDateTime.parse(isoFormat));
            } catch (Exception e) {
                System.err.println("Error parsing created_at: " + createdAt);
                user.setCreatedAt(LocalDateTime.now()); // fallback to current time
            }
        }
        
        String lastLogin = rs.getString("last_login");
        if (lastLogin != null) {
            try {
                // Replace space with 'T' to match ISO format
                String isoFormat = lastLogin.replace(' ', 'T');
                user.setLastLogin(LocalDateTime.parse(isoFormat));
            } catch (Exception e) {
                System.err.println("Error parsing last_login: " + lastLogin);
                user.setLastLogin(null); // fallback to null
            }
        }
        
        user.setCurrentStreak(rs.getInt("current_streak"));
        user.setTotalCO2Saved(rs.getDouble("total_co2_saved"));
        
        return user;
    }
    
    
    public void hashExistingPasswords() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, password FROM users");
            
            while (rs.next()) {
                int userId = rs.getInt("id");
                String storedPassword = rs.getString("password");
                
                System.out.println("User " + userId + " stored password: '" + storedPassword + "'");
                
                // If the password looks like it's already Base64 encoded (long), skip it
                if (storedPassword != null && storedPassword.length() >= 44) { // Base64 encoded are usually longer
                    try {
                        // Try to decode as Base64 to see if it's already our format
                        byte[] decoded = Base64.getDecoder().decode(storedPassword);
                        // Use the actual salt length from PasswordUtil (16 bytes)
                        if (decoded.length >= 16) { // 16 is SALT_LENGTH from PasswordUtil
                            System.out.println("Password for user " + userId + " appears to be already hashed with new algorithm, skipping");
                            continue;
                        }
                    } catch (IllegalArgumentException e) {
                        // Not valid Base64, so needs hashing
                    }
                }
                
                // This password needs to be re-hashed with the new algorithm
                String hashedPassword = PasswordUtil.hashPassword(storedPassword);
                
                // Update the database
                PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE users SET password = ? WHERE id = ?");
                updateStmt.setString(1, hashedPassword);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();
                
                System.out.println("Re-hashed password for user " + userId + ": " + hashedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}