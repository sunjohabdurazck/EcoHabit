package main.java.com.ecohabit.util;

import main.java.com.ecohabit.model.User;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class JwtTokenUtil {
    private static final String SECRET_KEY = "EcoHabitSecretKey2024!@#"; // Change this in production
    private static final long EXPIRATION_TIME = 86400000; // 24 hours
    
    public String generateToken(User user) {
        try {
            // Create header
            Map<String, String> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            
            // Create payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", user.getId());
            payload.put("email", user.getEmail());
            payload.put("firstName", user.getFirstName());
            payload.put("lastName", user.getLastName());
            payload.put("iat", System.currentTimeMillis());
            payload.put("exp", System.currentTimeMillis() + EXPIRATION_TIME);
            
            // Encode header and payload
            String headerEncoded = base64Encode(header.toString());
            String payloadEncoded = base64Encode(payload.toString());
            
            // Create signature
            String signature = createSignature(headerEncoded + "." + payloadEncoded);
            
            return headerEncoded + "." + payloadEncoded + "." + signature;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate token", e);
        }
    }
    
    public boolean validateToken(String token) {
        try {
            if (token == null || !token.contains(".")) {
                return false;
            }
            
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }
            
            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];
            
            // Verify signature
            String expectedSignature = createSignature(header + "." + payload);
            if (!signature.equals(expectedSignature)) {
                return false;
            }
            
            // Verify expiration
            String payloadJson = base64Decode(payload);
            if (!payloadJson.contains("\"exp\":")) {
                return false;
            }
            
            // Extract expiration time (simplified parsing)
            String expStr = payloadJson.split("\"exp\":")[1].split(",")[0].trim();
            long expirationTime = Long.parseLong(expStr);
            
            return System.currentTimeMillis() < expirationTime;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    public int getUserIdFromToken(String token) {
        try {
            String payload = token.split("\\.")[1];
            String payloadJson = base64Decode(payload);
            
            // Extract user ID (simplified parsing)
            String userIdStr = payloadJson.split("\"userId\":")[1].split(",")[0].trim();
            return Integer.parseInt(userIdStr);
            
        } catch (Exception e) {
            throw new RuntimeException("Invalid token", e);
        }
    }
    
    public String getEmailFromToken(String token) {
        try {
            String payload = token.split("\\.")[1];
            String payloadJson = base64Decode(payload);
            
            // Extract email (simplified parsing)
            String email = payloadJson.split("\"email\":\"")[1].split("\"")[0];
            return email;
            
        } catch (Exception e) {
            throw new RuntimeException("Invalid token", e);
        }
    }
    
    private String createSignature(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((data + SECRET_KEY).getBytes(StandardCharsets.UTF_8));
            return base64EncodeBytes(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    private String base64Encode(String data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }
    
    private String base64EncodeBytes(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }
    
    private String base64Decode(String data) {
        byte[] decoded = Base64.getUrlDecoder().decode(data);
        return new String(decoded, StandardCharsets.UTF_8);
    }
    
    /**
     * Extract all claims from token for debugging purposes
     */
    public Map<String, Object> getClaims(String token) {
        try {
            String payload = token.split("\\.")[1];
            String payloadJson = base64Decode(payload);
            
            // This is a simplified parser - in production, use a proper JSON parser
            Map<String, Object> claims = new HashMap<>();
            String[] pairs = payloadJson.replace("{", "").replace("}", "").split(",");
            
            for (String pair : pairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim();
                    
                    // Remove quotes from string values
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    
                    claims.put(key, value);
                }
            }
            
            return claims;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse token claims", e);
        }
    }
}