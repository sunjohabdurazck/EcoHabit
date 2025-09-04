package main.java.com.ecohabit.util;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;
import java.io.*;
import java.util.Arrays;

/**
 * Utility class for security operations including password hashing, data encryption,
 * input validation, and secure random generation
 */
public class SecurityUtil {
    
    // Constants for security configurations
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String AES_ALGORITHM = "AES";
    private static final int SALT_LENGTH = 32; // 256 bits
    private static final int IV_LENGTH = 12; // 96 bits for GCM
    private static final int TAG_LENGTH = 128; // 128 bits for GCM
    private static final int PBKDF2_ITERATIONS = 100000; // OWASP recommended minimum
    private static final int KEY_LENGTH = 256; // bits
    
    // Password validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    private final SecureRandom secureRandom;
    
    public SecurityUtil() {
        this.secureRandom = new SecureRandom();
    }
    
    /**
     * Hash a password using PBKDF2 with SHA-256
     * @param password The plain text password
     * @return Base64 encoded hash containing salt and hash
     */
    public String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        try {
            // Generate random salt
            byte[] salt = generateSalt();
            
            // Hash password with PBKDF2
            byte[] hashedPassword = pbkdf2Hash(password, salt, PBKDF2_ITERATIONS);
            
            // Combine salt and hash
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    /**
     * Verify a password against a stored hash
     * @param password The plain text password to verify
     * @param storedHash The stored hash to verify against
     * @return true if password matches, false otherwise
     */
    public boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }
        
        try {
            // Decode stored hash
            byte[] combined = Base64.getDecoder().decode(storedHash);
            
            if (combined.length <= SALT_LENGTH) {
                return false;
            }
            
            // Extract salt and hash
            byte[] salt = Arrays.copyOfRange(combined, 0, SALT_LENGTH);
            byte[] storedPasswordHash = Arrays.copyOfRange(combined, SALT_LENGTH, combined.length);
            
            // Hash provided password with same salt
            byte[] providedPasswordHash = pbkdf2Hash(password, salt, PBKDF2_ITERATIONS);
            
            // Compare hashes using constant-time comparison
            return MessageDigest.isEqual(storedPasswordHash, providedPasswordHash);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Generate a cryptographically secure random salt
     * @return Random byte array of SALT_LENGTH
     */
    private byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return salt;
    }
    
    /**
     * Hash password using PBKDF2
     * @param password The password to hash
     * @param salt The salt to use
     * @param iterations Number of iterations
     * @return Hashed password bytes
     */
    private byte[] pbkdf2Hash(String password, byte[] salt, int iterations) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(
            password.toCharArray(), 
            salt, 
            iterations, 
            KEY_LENGTH
        );
        
        SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        byte[] hash = factory.generateSecret(spec).getEncoded();
        spec.clearPassword(); // Clear sensitive data
        
        return hash;
    }
    
    /**
     * Encrypt data using AES-GCM
     * @param plainText The data to encrypt
     * @param password The password to derive encryption key from
     * @return Base64 encoded encrypted data with IV and salt
     */
    public String encryptData(String plainText, String password) {
        if (plainText == null || password == null) {
            throw new IllegalArgumentException("Plain text and password cannot be null");
        }
        
        try {
            // Generate random salt and IV
            byte[] salt = generateSalt();
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);
            
            // Derive key from password
            SecretKey key = deriveKeyFromPassword(password, salt);
            
            // Initialize cipher
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
            
            // Encrypt data
            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // Combine salt, IV, and encrypted data
            byte[] result = new byte[salt.length + iv.length + encryptedData.length];
            System.arraycopy(salt, 0, result, 0, salt.length);
            System.arraycopy(iv, 0, result, salt.length, iv.length);
            System.arraycopy(encryptedData, 0, result, salt.length + iv.length, encryptedData.length);
            
            return Base64.getEncoder().encodeToString(result);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt data", e);
        }
    }
    
    /**
     * Decrypt data using AES-GCM
     * @param encryptedData Base64 encoded encrypted data
     * @param password The password to derive decryption key from
     * @return Decrypted plain text
     */
    public String decryptData(String encryptedData, String password) {
        if (encryptedData == null || password == null) {
            throw new IllegalArgumentException("Encrypted data and password cannot be null");
        }
        
        try {
            // Decode encrypted data
            byte[] combined = Base64.getDecoder().decode(encryptedData);
            
            if (combined.length <= SALT_LENGTH + IV_LENGTH) {
                throw new IllegalArgumentException("Invalid encrypted data format");
            }
            
            // Extract salt, IV, and encrypted data
            byte[] salt = Arrays.copyOfRange(combined, 0, SALT_LENGTH);
            byte[] iv = Arrays.copyOfRange(combined, SALT_LENGTH, SALT_LENGTH + IV_LENGTH);
            byte[] cipherText = Arrays.copyOfRange(combined, SALT_LENGTH + IV_LENGTH, combined.length);
            
            // Derive key from password
            SecretKey key = deriveKeyFromPassword(password, salt);
            
            // Initialize cipher
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
            
            // Decrypt data
            byte[] decryptedData = cipher.doFinal(cipherText);
            
            return new String(decryptedData, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt data", e);
        }
    }
    
    /**
     * Derive encryption key from password using PBKDF2
     * @param password The password
     * @param salt The salt
     * @return Secret key for AES encryption
     */
    private SecretKey deriveKeyFromPassword(String password, byte[] salt) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(
            password.toCharArray(),
            salt,
            PBKDF2_ITERATIONS,
            KEY_LENGTH
        );
        
        SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        spec.clearPassword(); // Clear sensitive data
        
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }
    
    /**
     * Generate a secure random string for tokens, session IDs, etc.
     * @param length The length of the random string
     * @return Base64 encoded random string
     */
    public String generateSecureRandomString(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
    
    /**
     * Generate a secure random number within a range
     * @param min Minimum value (inclusive)
     * @param max Maximum value (exclusive)
     * @return Random number in range
     */
    public int generateSecureRandomInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("Min must be less than max");
        }
        return secureRandom.nextInt(max - min) + min;
    }
    
    /**
     * Hash data using SHA-256
     * @param data The data to hash
     * @return Base64 encoded hash
     */
    public String hashData(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not available", e);
        }
    }
    
    /**
     * Hash data with salt using SHA-256
     * @param data The data to hash
     * @param salt The salt to use
     * @return Base64 encoded hash
     */
    public String hashDataWithSalt(String data, String salt) {
        if (data == null || salt == null) {
            throw new IllegalArgumentException("Data and salt cannot be null");
        }
        
        return hashData(data + salt);
    }
    
    /**
     * Validate email format
     * @param email The email to validate
     * @return true if valid email format
     */
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate password strength
     * @param password The password to validate
     * @return true if password meets strength requirements
     */
    public boolean isStrongPassword(String password) {
        return password != null && STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Get password strength score (0-4)
     * @param password The password to score
     * @return Strength score: 0=Very Weak, 1=Weak, 2=Fair, 3=Good, 4=Strong
     */
    public int getPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }
        
        int score = 0;
        
        // Length check
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        
        // Character type checks
        if (password.matches(".*[a-z].*")) score++; // lowercase
        if (password.matches(".*[A-Z].*")) score++; // uppercase
        if (password.matches(".*\\d.*")) score++; // digits
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) score++; // special chars
        
        // Deduct points for common patterns
        if (password.matches(".*(..).*\\1.*")) score--; // repeated patterns
        if (password.matches(".*(?:123|abc|qwe).*")) score--; // common sequences
        
        return Math.max(0, Math.min(4, score - 2)); // Normalize to 0-4 scale
    }
    
    /**
     * Sanitize input string to prevent injection attacks
     * @param input The input string to sanitize
     * @return Sanitized string
     */
    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        return input
            .replaceAll("[<>\"'&]", "") // Remove potentially dangerous characters
            .replaceAll("\\s+", " ") // Normalize whitespace
            .trim(); // Remove leading/trailing whitespace
    }
    
    /**
     * Validate that a string contains only alphanumeric characters and common punctuation
     * @param input The string to validate
     * @return true if safe, false otherwise
     */
    public boolean isSafeString(String input) {
        if (input == null) {
            return true;
        }
        
        // Allow alphanumeric, spaces, and common punctuation
        return input.matches("^[a-zA-Z0-9\\s.,!?@#$%^&*()\\-_+=\\[\\]{}|;:'\"/\\\\]*$");
    }
    
    /**
     * Generate a secure session token
     * @return Base64 encoded session token
     */
    public String generateSessionToken() {
        return generateSecureRandomString(32);
    }
    
    /**
     * Validate session token format
     * @param token The token to validate
     * @return true if valid format
     */
    public boolean isValidSessionToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(token);
            return decoded.length >= 24; // At least 192 bits
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Create a hash-based message authentication code (HMAC)
     * @param message The message to authenticate
     * @param key The secret key
     * @return Base64 encoded HMAC
     */
    public String createHMAC(String message, String key) {
        if (message == null || key == null) {
            throw new IllegalArgumentException("Message and key cannot be null");
        }
        
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8), 
                "HmacSHA256"
            );
            mac.init(secretKey);
            
            byte[] hmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmac);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create HMAC", e);
        }
    }
    
    /**
     * Verify an HMAC
     * @param message The original message
     * @param key The secret key
     * @param expectedHmac The HMAC to verify
     * @return true if HMAC is valid
     */
    public boolean verifyHMAC(String message, String key, String expectedHmac) {
        try {
            String computedHmac = createHMAC(message, key);
            return MessageDigest.isEqual(
                computedHmac.getBytes(StandardCharsets.UTF_8),
                expectedHmac.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Encrypt a file
     * @param inputFile The file to encrypt
     * @param outputFile The encrypted output file
     * @param password The encryption password
     * @throws IOException If file operations fail
     */
    public void encryptFile(File inputFile, File outputFile, String password) throws IOException {
        if (!inputFile.exists()) {
            throw new FileNotFoundException("Input file not found: " + inputFile.getAbsolutePath());
        }
        
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            
            // Generate salt and IV
            byte[] salt = generateSalt();
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);
            
            // Write salt and IV to output file
            fos.write(salt);
            fos.write(iv);
            
            // Derive key
            SecretKey key = deriveKeyFromPassword(password, salt);
            
            // Initialize cipher
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
            
            // Encrypt file in chunks
            try (CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    cos.write(buffer, 0, bytesRead);
                }
            }
            
        } catch (Exception e) {
            throw new IOException("Failed to encrypt file", e);
        }
    }
    
    /**
     * Decrypt a file
     * @param inputFile The encrypted file
     * @param outputFile The decrypted output file
     * @param password The decryption password
     * @throws IOException If file operations fail
     */
    public void decryptFile(File inputFile, File outputFile, String password) throws IOException {
        if (!inputFile.exists()) {
            throw new FileNotFoundException("Input file not found: " + inputFile.getAbsolutePath());
        }
        
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            
            // Read salt and IV
            byte[] salt = new byte[SALT_LENGTH];
            byte[] iv = new byte[IV_LENGTH];
            
            if (fis.read(salt) != SALT_LENGTH || fis.read(iv) != IV_LENGTH) {
                throw new IOException("Invalid encrypted file format");
            }
            
            // Derive key
            SecretKey key = deriveKeyFromPassword(password, salt);
            
            // Initialize cipher
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
            
            // Decrypt file
            try (CipherInputStream cis = new CipherInputStream(fis, cipher)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            
        } catch (Exception e) {
            throw new IOException("Failed to decrypt file", e);
        }
    }
    
    /**
     * Securely wipe sensitive data from memory
     * @param sensitiveData Array containing sensitive data to wipe
     */
    public void wipeSensitiveData(char[] sensitiveData) {
        if (sensitiveData != null) {
            Arrays.fill(sensitiveData, '\0');
        }
    }
    
    /**
     * Securely wipe sensitive data from memory
     * @param sensitiveData Array containing sensitive data to wipe
     */
    public void wipeSensitiveData(byte[] sensitiveData) {
        if (sensitiveData != null) {
            Arrays.fill(sensitiveData, (byte) 0);
        }
    }
    
    /**
     * Check if the current JVM supports strong cryptography
     * @return true if unlimited strength cryptography is available
     */
    public boolean isUnlimitedCryptographyAvailable() {
        try {
            return Cipher.getMaxAllowedKeyLength("AES") >= 256;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
    
    /**
     * Get security algorithm information
     * @return Map containing security algorithm details
     */
    public java.util.Map<String, String> getSecurityInfo() {
        java.util.Map<String, String> info = new java.util.HashMap<>();
        
        info.put("hashAlgorithm", HASH_ALGORITHM);
        info.put("pbkdf2Algorithm", PBKDF2_ALGORITHM);
        info.put("encryptionAlgorithm", AES_TRANSFORMATION);
        info.put("keyLength", String.valueOf(KEY_LENGTH));
        info.put("saltLength", String.valueOf(SALT_LENGTH));
        info.put("pbkdf2Iterations", String.valueOf(PBKDF2_ITERATIONS));
        info.put("unlimitedCrypto", String.valueOf(isUnlimitedCryptographyAvailable()));
        
        return info;
    }
}