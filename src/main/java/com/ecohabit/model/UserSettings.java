package main.java.com.ecohabit.model;

/**
 * Model class representing user settings and preferences
 */
public class UserSettings {
    
    // Profile Information
    private String firstName;
    private String lastName;
    private String email;
    private int age;
    private String gender;
    private String location;
    
    // Lifestyle Preferences
    private String dietType;
    private String transportMethod;
    private String userType;
    
    // Appearance Settings
    private String theme;
    private int fontSize;
    private boolean highContrast;
    
    // Notification Settings
    private boolean dailyReminder;
    private boolean weeklySummary;
    private boolean achievementNotifications;
    private boolean ecoTips;
    private String notificationSound;
    
    // Privacy Settings
    private boolean dataCollection;
    private boolean personalizedAds;
    
    /**
     * Default constructor with default values
     */
    public UserSettings() {
        this.firstName = "John";
        this.lastName = "Doe";
        this.email = "john.doe@example.com";
        this.age = 25;
        this.gender = "Male";
        this.location = "";
        
        this.dietType = "Omnivore";
        this.transportMethod = "Mixed";
        this.userType = "Beginner";
        
        this.theme = "eco";
        this.fontSize = 14;
        this.highContrast = false;
        
        this.dailyReminder = true;
        this.weeklySummary = true;
        this.achievementNotifications = true;
        this.ecoTips = true;
        this.notificationSound = "Default";
        
        this.dataCollection = false;
        this.personalizedAds = false;
    }
    
    // Getters and Setters
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
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getDietType() {
        return dietType;
    }
    
    public void setDietType(String dietType) {
        this.dietType = dietType;
    }
    
    public String getTransportMethod() {
        return transportMethod;
    }
    
    public void setTransportMethod(String transportMethod) {
        this.transportMethod = transportMethod;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public String getTheme() {
        return theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    public int getFontSize() {
        return fontSize;
    }
    
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
    
    public boolean isHighContrast() {
        return highContrast;
    }
    
    public void setHighContrast(boolean highContrast) {
        this.highContrast = highContrast;
    }
    
    public boolean isDailyReminder() {
        return dailyReminder;
    }
    
    public void setDailyReminder(boolean dailyReminder) {
        this.dailyReminder = dailyReminder;
    }
    
    public boolean isWeeklySummary() {
        return weeklySummary;
    }
    
    public void setWeeklySummary(boolean weeklySummary) {
        this.weeklySummary = weeklySummary;
    }
    
    public boolean isAchievementNotifications() {
        return achievementNotifications;
    }
    
    public void setAchievementNotifications(boolean achievementNotifications) {
        this.achievementNotifications = achievementNotifications;
    }
    
    public boolean isEcoTips() {
        return ecoTips;
    }
    
    public void setEcoTips(boolean ecoTips) {
        this.ecoTips = ecoTips;
    }
    
    public String getNotificationSound() {
        return notificationSound;
    }
    
    public void setNotificationSound(String notificationSound) {
        this.notificationSound = notificationSound;
    }
    
    public boolean isDataCollection() {
        return dataCollection;
    }
    
    public void setDataCollection(boolean dataCollection) {
        this.dataCollection = dataCollection;
    }
    
    public boolean isPersonalizedAds() {
        return personalizedAds;
    }
    
    public void setPersonalizedAds(boolean personalizedAds) {
        this.personalizedAds = personalizedAds;
    }
    
    @Override
    public String toString() {
        return "UserSettings{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", theme='" + theme + '\'' +
                ", fontSize=" + fontSize +
                '}';
    }
}