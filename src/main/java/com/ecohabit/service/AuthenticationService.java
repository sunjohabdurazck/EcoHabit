package main.java.com.ecohabit.service;

import main.java.com.ecohabit.model.User;
import main.java.com.ecohabit.util.PasswordUtil;
import java.time.LocalDateTime;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

public class AuthenticationService {
    private static AuthenticationService instance;
    private DatabaseService databaseService;
    
    private AuthenticationService() {
        this.databaseService = DatabaseService.getInstance();
    }
    
    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        } 
        return instance;
    }
    

    public User login(String email, String password) {
        System.out.println("Login attempt for email: " + email);
        
        User user = databaseService.getUserByEmail(email);
        if (user == null) {
            System.out.println("User not found");
            return null;
        }
        
        System.out.println("User found: " + (user != null));
        System.out.println("Entered password: '" + password + "'");
        System.out.println("Stored hash: '" + user.getHashedPassword() + "'");
        System.out.println("Hash length: " + (user.getHashedPassword() != null ? user.getHashedPassword().length() : 0));
        System.out.println("Is our algorithm: " + PasswordUtil.isPasswordHashedWithOurAlgorithm(user.getHashedPassword()));
        
        // === ADD DEBUG CODE HERE ===
        System.out.println("=== DEBUG: Testing current hashing algorithm ===");
        String testHash = PasswordUtil.hashPassword(password);
        System.out.println("Current algorithm hash for '" + password + "': " + testHash);
        System.out.println("Current hash length: " + testHash.length());
        
        // Test if current algorithm can verify its own hash
        boolean selfTest = PasswordUtil.verifyPassword(password, testHash);
        System.out.println("Self-verification test: " + selfTest);
        System.out.println("=== END DEBUG ===");
        // === END DEBUG CODE ===
        
        // Use verifyPassword instead of comparing hashes directly
        boolean passwordMatches = PasswordUtil.verifyPassword(password, user.getHashedPassword());
        
        System.out.println("Password matches: " + passwordMatches);
        
        if (passwordMatches) {
            // Update last login
            user.setLastLogin(LocalDateTime.now());
            databaseService.updateUser(user);
            return user;
        }
        
        return null;
    }
    
    public boolean isDatabaseInitialized() {
        return databaseService.isInitialized();
    }
    
    // ✅ Registration
    public boolean register(User user, String password) {
        if (databaseService.emailExists(user.getEmail())) {
            return false;
        }
        String hashedPassword = PasswordUtil.hashPassword(password);
        user.setHashedPassword(hashedPassword);
        user.setCreatedAt(LocalDateTime.now());
        return databaseService.saveUser(user);
    }
    
    // ✅ Google login
    public User loginWithGoogle(String authCode) {
        try {
            GoogleAuthService googleAuth = GoogleAuthService.getInstance();
            GoogleTokenResponse credential = googleAuth.exchangeCodeForTokens(authCode);
            User user = googleAuth.getUserInfo(credential);

            User existingUser = databaseService.getUserByEmail(user.getEmail());
            if (existingUser != null) {
                return existingUser;
            } else {
                user.setHashedPassword(null); // Google users don't have password
                databaseService.saveUser(user);
                return user;
            }
        } catch (Exception e) {
            error("Google login failed", e);
            return null;
        }
    }

    private void error(String msg, Exception e) {
        System.err.println(msg);
        e.printStackTrace();
    }
}
