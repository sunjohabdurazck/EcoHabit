package main.java.com.ecohabit.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        // Add more password complexity rules if needed
        return true;
    }
    
    public static String getPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return "Weak - At least 6 characters required";
        }
        
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score++;
        
        return switch (score) {
            case 0, 1 -> "Weak";
            case 2, 3 -> "Medium";
            case 4, 5 -> "Strong";
            default -> "Very Strong";
        };
    }
}
