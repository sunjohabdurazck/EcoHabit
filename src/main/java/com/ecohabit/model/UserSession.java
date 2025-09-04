package main.java.com.ecohabit.model;

public class UserSession {
    private static UserSession instance;
    private static User currentUser;
    private boolean isLoggedIn;
    
    private UserSession() {
        this.isLoggedIn = false;
    }
    
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    public void login(User user) {
        this.currentUser = user;
        this.isLoggedIn = true;
    }
    
    public void logout() {
        this.currentUser = null;
        this.isLoggedIn = false;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    
}
