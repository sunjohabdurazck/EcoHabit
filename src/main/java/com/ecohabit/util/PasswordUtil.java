
package main.java.com.ecohabit.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final PasswordHasher passwordHasher = new PasswordHasher();
    
    public static String hashPassword(String password) {
        try {
            // Generate salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash password with salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Combine salt and hash
            byte[] saltedHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltedHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltedHash, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(saltedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            System.out.println("=== PASSWORD VERIFICATION DEBUG ===");
            System.out.println("Input password: '" + password + "'");
            System.out.println("Stored hash: '" + hashedPassword + "'");
            System.out.println("Hash contains colons: " + (hashedPassword != null && hashedPassword.contains(":")));
            
            // First try the new PasswordHasher format (contains colons)
            if (hashedPassword != null && hashedPassword.contains(":")) {
                System.out.println("Trying PasswordHasher format...");
                boolean result = passwordHasher.verify(password, hashedPassword);
                System.out.println("PasswordHasher result: " + result);
                return result;
            }
            
            // NEW: Try direct comparison for plain text or other formats
            if (hashedPassword != null && hashedPassword.equals(password)) {
                System.out.println("Direct comparison match - plain text detected");
                return true;
            }
            
            // NEW: Try current hashing algorithm for consistency
            String currentHash = hashPasswordWithCurrentAlgorithm(password);
            if (hashedPassword != null && hashedPassword.equals(currentHash)) {
                System.out.println("Current algorithm match");
                return true;
            }
            
            System.out.println("Trying SHA-256 format...");
            // Then try the old SHA-256 format
            try {
                byte[] saltedHash = Base64.getDecoder().decode(hashedPassword);
                System.out.println("Decoded hash length: " + saltedHash.length);
                
                // Extract salt
                byte[] salt = new byte[SALT_LENGTH];
                System.arraycopy(saltedHash, 0, salt, 0, SALT_LENGTH);
                
                // Hash the provided password with the extracted salt
                MessageDigest md = MessageDigest.getInstance(ALGORITHM);
                md.update(salt);
                byte[] hashedProvidedPassword = md.digest(password.getBytes());
                
                // Compare the hashes
                byte[] storedHash = new byte[saltedHash.length - SALT_LENGTH];
                System.arraycopy(saltedHash, SALT_LENGTH, storedHash, 0, storedHash.length);
                
                boolean result = MessageDigest.isEqual(hashedProvidedPassword, storedHash);
                System.out.println("SHA-256 result: " + result);
                return result;
            } catch (Exception e) {
                System.out.println("SHA-256 verification failed: " + e.getMessage());
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("Verification failed with exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Add this helper method
    private static String hashPasswordWithCurrentAlgorithm(String password) {
        try {
            // This should match whatever your current registration uses
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static boolean isPasswordHashedWithOurAlgorithm(String hashedPassword) {
        if (hashedPassword == null) {
            return false;
        }
        
        // Check for new format (contains colons)
        if (hashedPassword.contains(":")) {
            try {
                String[] parts = hashedPassword.split(":");
                return parts.length == 3 && 
                       parts[0].matches("\\d+") && // iterations is a number
                       parts[1].length() >= 16 &&  // salt is reasonable length
                       parts[2].length() >= 32;    // hash is reasonable length
            } catch (Exception e) {
                return false;
            }
        }
        
        // Check for old format
        try {
            byte[] decoded = Base64.getDecoder().decode(hashedPassword);
            return decoded.length == 48; // SALT_LENGTH + 32
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}