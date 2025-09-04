package main.java.com.ecohabit.model;

public class AuthenticationResult {
    private final boolean success;
    private final String message;
    private final User user;
    private final String token;
    
    private AuthenticationResult(boolean success, String message, User user, String token) {
        this.success = success;
        this.message = message;
        this.user = user;
        this.token = token;
    }
    
    public static AuthenticationResult success(User user, String token, String message) {
        return new AuthenticationResult(true, message, user, token);
    }
    
    public static AuthenticationResult failure(String message) {
        return new AuthenticationResult(false, message, null, null);
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public User getUser() { return user; }
    public String getToken() { return token; }
}