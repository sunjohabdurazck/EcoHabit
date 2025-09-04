package main.java.com.ecohabit.util;

import java.util.regex.Pattern;

public class PasswordValidator {

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");
    
    public static boolean isValid(String password) {
        // Check minimum length
        if (password == null || password.length() < 8) {
            return false;
        }
        
        // Check for required character types
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            return false;
        }
        
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            return false;
        }
        
        if (!DIGIT_PATTERN.matcher(password).find()) {
            return false;
        }
        
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            return false;
        }
        
        return true;
    }
    
    public static String getValidationFeedback(String password) {
        if (password == null) {
            return "Password cannot be null";
        }
        
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        
        StringBuilder feedback = new StringBuilder();
        
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            feedback.append("• At least one uppercase letter (A-Z) is required\n");
        }
        
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            feedback.append("• At least one lowercase letter (a-z) is required\n");
        }
        
        if (!DIGIT_PATTERN.matcher(password).find()) {
            feedback.append("• At least one digit (0-9) is required\n");
        }
        
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            feedback.append("• At least one special character (!@#$%^&* etc.) is required\n");
        }
        
        if (feedback.length() == 0) {
            return "Password meets all requirements";
        }
        
        return "Password requirements not met:\n" + feedback.toString();
    }
}