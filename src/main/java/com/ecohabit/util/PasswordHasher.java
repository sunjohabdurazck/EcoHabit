package main.java.com.ecohabit.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordHasher {
    private static final int SALT_LENGTH = 16; // 16 bytes = 128 bits
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256; // 256 bits = 32 bytes
    
    // Algorithm: PBKDF2WithHmacSHA256 is available in standard Java
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    
    /**
     * Hash a password with a randomly generated salt
     */
    public String hash(String password) {
        try {
            // Generate random salt
            byte[] salt = generateSalt();
            
            // Hash the password
            byte[] hash = hashPassword(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            
            // Combine salt and hash for storage
            return encodeForStorage(salt, hash, ITERATIONS);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    /**
     * Verify a password against a stored hash
     */
    public boolean verify(String password, String storedHash) {
        try {
            // Decode the stored hash to extract components
            HashComponents components = decodeFromStorage(storedHash);
            
            // Hash the provided password with the same parameters
            byte[] testHash = hashPassword(
                password.toCharArray(), 
                components.salt, 
                components.iterations, 
                components.keyLength
            );
            
            // Compare the hashes
            return constantTimeEquals(components.hash, testHash);
            
        } catch (Exception e) {
            // Log the error but don't reveal details to prevent timing attacks
            System.err.println("Password verification failed: " + e.getMessage());
            // Still perform a hash operation to prevent timing attacks
            hashPassword("dummy".toCharArray(), new byte[SALT_LENGTH], ITERATIONS, KEY_LENGTH);
            return false;
        }
    }
    
    /**
     * Generate a cryptographically secure random salt
     */
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }
    
    /**
     * Hash the password using PBKDF2
     */
    private byte[] hashPassword(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    /**
     * Encode salt, hash, and parameters for storage
     * Format: iterations:salt:hash (all base64 encoded)
     */
    private String encodeForStorage(byte[] salt, byte[] hash, int iterations) {
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);
        return iterations + ":" + saltBase64 + ":" + hashBase64;
    }
    
    /**
     * Decode stored hash into its components
     */
    private HashComponents decodeFromStorage(String storedHash) {
        String[] parts = storedHash.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid hash format");
        }
        
        try {
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] hash = Base64.getDecoder().decode(parts[2]);
            
            return new HashComponents(salt, hash, iterations, KEY_LENGTH);
            
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid hash format", e);
        }
    }
    
    /**
     * Constant-time comparison to prevent timing attacks
     */
    private boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        
        return result == 0;
    }
    
    /**
     * Helper class to store hash components
     */
    private static class HashComponents {
        public final byte[] salt;
        public final byte[] hash;
        public final int iterations;
        public final int keyLength;
        
        public HashComponents(byte[] salt, byte[] hash, int iterations, int keyLength) {
            this.salt = salt;
            this.hash = hash;
            this.iterations = iterations;
            this.keyLength = keyLength;
        }
    }
    
    /**
     * Check if a password meets complexity requirements
     */
    public boolean meetsComplexityRequirements(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            if (Character.isLowerCase(c)) hasLowerCase = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        
        // Require at least 3 of the 4 complexity criteria
        int criteriaMet = 0;
        if (hasUpperCase) criteriaMet++;
        if (hasLowerCase) criteriaMet++;
        if (hasDigit) criteriaMet++;
        if (hasSpecial) criteriaMet++;
        
        return criteriaMet >= 3;
    }
    
    /**
     * Generate a random password
     */
    public String generateRandomPassword(int length) {
        if (length < 8) length = 8;
        
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()-_=+[]{}|;:,.<>?";
        
        String allChars = upperCase + lowerCase + digits + special;
        SecureRandom random = new SecureRandom();
        
        StringBuilder password = new StringBuilder(length);
        
        // Ensure at least one character from each category
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));
        
        // Fill the rest with random characters
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Shuffle the password
        return shuffleString(password.toString());
    }
    
    /**
     * Shuffle a string randomly
     */
    private String shuffleString(String input) {
        char[] characters = input.toCharArray();
        SecureRandom random = new SecureRandom();
        
        for (int i = characters.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = characters[index];
            characters[index] = characters[i];
            characters[i] = temp;
        }
        
        return new String(characters);
    }
}