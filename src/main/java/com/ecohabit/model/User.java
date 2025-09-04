// User.java
package main.java.com.ecohabit.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model class representing a user in the EcoHabit application
 */
public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate registrationDate;
    private int currentStreak;
    private double totalCO2Saved;
    private int totalActivities;
    private String profilePicture;
    private boolean isActive;
    private LocalDate lastActivityDate;
    private int longestStreak;
    private String userLevel; // Beginner, Intermediate, Advanced, etc.
    
    // Additional fields that were referenced but not declared
    private int age;
    private String gender;
    private String dietType;
    private String transportationMethod;
    private String userType;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private String hashedPassword;
    private String authProvider;
    private String username;
    private String dietPreference;
    
    /**
     * Default constructor
     */
    public User() {
        this.registrationDate = LocalDate.now();
        this.currentStreak = 0;
        this.totalCO2Saved = 0.0;
        this.totalActivities = 0;
        this.isActive = true;
        this.longestStreak = 0;
        this.userLevel = "Beginner";
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with basic user information
     */
    public User(String firstName, String lastName, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    
    /**
     * Full constructor
     */
    public User(int id, String firstName, String lastName, String email, 
                LocalDate registrationDate, int currentStreak, double totalCO2Saved) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationDate = registrationDate;
        this.currentStreak = currentStreak;
        this.totalCO2Saved = totalCO2Saved;
        this.totalActivities = 0;
        this.isActive = true;
        this.longestStreak = currentStreak;
        this.userLevel = "Beginner";
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getDietType() { return dietType; }
    public void setDietType(String dietType) { this.dietType = dietType; }
    
    public String getTransportationMethod() { return transportationMethod; }
    public void setTransportationMethod(String transportationMethod) { this.transportationMethod = transportationMethod; }
    
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; } 
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    public String getHashedPassword() { return hashedPassword; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }
    
    public String getAuthProvider() { return authProvider; }
    public void setAuthProvider(String authProvider) { this.authProvider = authProvider; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public int getId() {
        return id;
    }
    public void setPasswordHash(String passwordHash) {
        this.hashedPassword = passwordHash;
    }
    
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public int getCurrentStreak() {
        return currentStreak;
    }
    
    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
        // Update longest streak if current streak exceeds it
        if (currentStreak > longestStreak) {
            this.longestStreak = currentStreak;
        }
    }
    public String getInitials() {
        if (firstName != null && !firstName.isEmpty()) {
            if (lastName != null && !lastName.isEmpty()) {
                // First letter of first name + first letter of last name
                return String.valueOf(firstName.charAt(0)).toUpperCase() + 
                       String.valueOf(lastName.charAt(0)).toUpperCase();
            } else {
                // First two letters of first name (or just first letter if only one character)
                return firstName.length() > 1 ? 
                    firstName.substring(0, 2).toUpperCase() : 
                    firstName.toUpperCase();
            }
        }
        return "US"; // Default if no name
    }
   
    public double getTotalCO2Saved() {
        return totalCO2Saved;
    }
    
    public void setTotalCO2Saved(double totalCO2Saved) {
        this.totalCO2Saved = totalCO2Saved;
    }
    
    public int getTotalActivities() {
        return totalActivities;
    }
    
    public void setTotalActivities(int totalActivities) {
        this.totalActivities = totalActivities;
    }
    
    public String getProfilePicture() {
        return profilePicture;
    }
    
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public LocalDate getLastActivityDate() {
        return lastActivityDate;
    }
    
    public void setLastActivityDate(LocalDate lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
        
        // Update streak based on activity date
        updateStreak();
    }
    
    public int getLongestStreak() {
        return longestStreak;
    }
    
    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }
    
    public String getUserLevel() {
        return userLevel;
    }
    
    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }
    
    // Utility Methods
    
    /**
     * Get full name of the user
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Add CO2 saved to the total
     */
    public void addCO2Saved(double co2Amount) {
        this.totalCO2Saved += co2Amount;
    }
    
    /**
     * Increment total activities count
     */
    public void incrementActivityCount() {
        this.totalActivities++;
        updateUserLevel();
    }
    
    /**
     * Update streak based on last activity date
     */
    private void updateStreak() {
        if (lastActivityDate == null) {
            return;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        
        if (lastActivityDate.equals(today) || lastActivityDate.equals(yesterday)) {
            // Continue or maintain streak
            if (lastActivityDate.equals(today) && !lastActivityDate.equals(getLastActivityDate())) {
                currentStreak++;
                if (currentStreak > longestStreak) {
                    longestStreak = currentStreak;
                }
            }
        } else if (lastActivityDate.isBefore(yesterday)) {
            // Streak broken
            currentStreak = 0;
        }
    }
    
    /**
     * Update user level based on total activities
     */
    private void updateUserLevel() {
        if (totalActivities >= 100) {
            userLevel = "Environmental Expert";
        } else if (totalActivities >= 50) {
            userLevel = "Advanced";
        } else if (totalActivities >= 20) {
            userLevel = "Intermediate";
        } else {
            userLevel = "Beginner";
        }
    }

    
    // ... existing methods ...
    
    public String getDietPreference() {
        return dietPreference;
    }
    
    public void setDietPreference(String dietPreference) {
        this.dietPreference = dietPreference;
    }
    /**
     * Check if user is new (registered within last 7 days)
     */
    public boolean isNewUser() {
        return registrationDate != null && 
               registrationDate.isAfter(LocalDate.now().minusDays(7));
    }
    
    /**
     * Get days since registration
     */
    public long getDaysSinceRegistration() {
        if (registrationDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(registrationDate, LocalDate.now());
    }
    
    /**
     * Calculate average CO2 saved per day
     */
    public double getAverageCO2PerDay() {
        long days = getDaysSinceRegistration();
        if (days == 0) {
            return totalCO2Saved;
        }
        return totalCO2Saved / days;
    }
    
    /**
     * Calculate average activities per day
     */
    public double getAverageActivitiesPerDay() {
        long days = getDaysSinceRegistration();
        if (days == 0) {
            return totalActivities;
        }
        return (double) totalActivities / days;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", registrationDate=" + registrationDate +
                ", currentStreak=" + currentStreak +
                ", totalCO2Saved=" + totalCO2Saved +
                ", totalActivities=" + totalActivities +
                ", userLevel='" + userLevel + '\'' +
                ", isActive=" + isActive +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        User user = (User) obj;
        return id == user.id && 
               email != null && email.equals(user.email);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, email);
    }

	public void setTransportPreference(String transport) {
		// TODO Auto-generated method stsub
		
	}

	public String getTransportPreference() {
		// TODO Auto-generated method stub
		return null;
	}
}