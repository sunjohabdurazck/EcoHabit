package main.java.com.ecohabit.service;

import main.java.com.ecohabit.model.User;
import main.java.com.ecohabit.model.UserSettings;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Service class for managing user settings and preferences
 */
public class SettingsService {
    
    private static final Logger logger = Logger.getLogger(SettingsService.class.getName());
    private static final String SETTINGS_FILE = "user_settings.properties";
    private static final String BACKUP_DIR = "backups";
    
    private UserSettings currentSettings;
    private Properties properties;
    
    public SettingsService() {
        this.properties = new Properties();
        this.currentSettings = new UserSettings();
        loadSettings();
    }
    
    /**
     * Get current user settings
     */
    public UserSettings getCurrentUserSettings() {
        return currentSettings;
    }
    
    /**
     * Save user settings to file
     */
    public void saveUserSettings(UserSettings settings) throws Exception {
        try {
            this.currentSettings = settings;
            
            // Convert settings to properties
            properties.setProperty("firstName", settings.getFirstName());
            properties.setProperty("lastName", settings.getLastName());
            properties.setProperty("email", settings.getEmail());
            properties.setProperty("age", String.valueOf(settings.getAge()));
            properties.setProperty("gender", settings.getGender());
            properties.setProperty("location", settings.getLocation());
            
            properties.setProperty("dietType", settings.getDietType());
            properties.setProperty("transportMethod", settings.getTransportMethod());
            properties.setProperty("userType", settings.getUserType());
            
            properties.setProperty("theme", settings.getTheme());
            properties.setProperty("fontSize", String.valueOf(settings.getFontSize()));
            properties.setProperty("highContrast", String.valueOf(settings.isHighContrast()));
            
            properties.setProperty("dailyReminder", String.valueOf(settings.isDailyReminder()));
            properties.setProperty("weeklySummary", String.valueOf(settings.isWeeklySummary()));
            properties.setProperty("achievementNotifications", String.valueOf(settings.isAchievementNotifications()));
            properties.setProperty("ecoTips", String.valueOf(settings.isEcoTips()));
            properties.setProperty("notificationSound", settings.getNotificationSound());
            
            properties.setProperty("dataCollection", String.valueOf(settings.isDataCollection()));
            properties.setProperty("personalizedAds", String.valueOf(settings.isPersonalizedAds()));
            
            // Save to file
            try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
                properties.store(fos, "EcoHabit User Settings - " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            
            logger.info("User settings saved successfully");
            
        } catch (IOException e) {
            logger.severe("Failed to save user settings: " + e.getMessage());
            throw new Exception("Failed to save settings: " + e.getMessage());
        }
    }
    
    /**
     * Load user settings from file
     */
    private void loadSettings() {
        try {
            File settingsFile = new File(SETTINGS_FILE);
            if (settingsFile.exists()) {
                try (FileInputStream fis = new FileInputStream(settingsFile)) {
                    properties.load(fis);
                    
                    // Load settings from properties
                    currentSettings.setFirstName(properties.getProperty("firstName", "John"));
                    currentSettings.setLastName(properties.getProperty("lastName", "Doe"));
                    currentSettings.setEmail(properties.getProperty("email", "john.doe@example.com"));
                    currentSettings.setAge(Integer.parseInt(properties.getProperty("age", "25")));
                    currentSettings.setGender(properties.getProperty("gender", "Male"));
                    currentSettings.setLocation(properties.getProperty("location", ""));
                    
                    currentSettings.setDietType(properties.getProperty("dietType", "Omnivore"));
                    currentSettings.setTransportMethod(properties.getProperty("transportMethod", "Mixed"));
                    currentSettings.setUserType(properties.getProperty("userType", "Beginner"));
                    
                    currentSettings.setTheme(properties.getProperty("theme", "eco"));
                    currentSettings.setFontSize(Integer.parseInt(properties.getProperty("fontSize", "14")));
                    currentSettings.setHighContrast(Boolean.parseBoolean(properties.getProperty("highContrast", "false")));
                    
                    currentSettings.setDailyReminder(Boolean.parseBoolean(properties.getProperty("dailyReminder", "true")));
                    currentSettings.setWeeklySummary(Boolean.parseBoolean(properties.getProperty("weeklySummary", "true")));
                    currentSettings.setAchievementNotifications(Boolean.parseBoolean(properties.getProperty("achievementNotifications", "true")));
                    currentSettings.setEcoTips(Boolean.parseBoolean(properties.getProperty("ecoTips", "true")));
                    currentSettings.setNotificationSound(properties.getProperty("notificationSound", "Default"));
                    
                    currentSettings.setDataCollection(Boolean.parseBoolean(properties.getProperty("dataCollection", "false")));
                    currentSettings.setPersonalizedAds(Boolean.parseBoolean(properties.getProperty("personalizedAds", "false")));
                    
                    logger.info("User settings loaded successfully");
                }
            } else {
                logger.info("Settings file not found, using defaults");
            }
            
        } catch (Exception e) {
            logger.warning("Failed to load user settings, using defaults: " + e.getMessage());
            currentSettings = new UserSettings(); // Reset to defaults
        }
    }
    
    /**
     * Export data to CSV format
     */
    public String exportDataToCSV() throws Exception {
        try {
            String filename = "ecohabit_data_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
            
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write("Setting,Value\n");
                writer.write("First Name," + currentSettings.getFirstName() + "\n");
                writer.write("Last Name," + currentSettings.getLastName() + "\n");
                writer.write("Email," + currentSettings.getEmail() + "\n");
                writer.write("Age," + currentSettings.getAge() + "\n");
                writer.write("Gender," + currentSettings.getGender() + "\n");
                writer.write("Location," + currentSettings.getLocation() + "\n");
                writer.write("Diet Type," + currentSettings.getDietType() + "\n");
                writer.write("Transport Method," + currentSettings.getTransportMethod() + "\n");
                writer.write("User Type," + currentSettings.getUserType() + "\n");
                writer.write("Theme," + currentSettings.getTheme() + "\n");
                writer.write("Font Size," + currentSettings.getFontSize() + "\n");
            }
            
            logger.info("Data exported to CSV: " + filename);
            return new File(filename).getAbsolutePath();
            
        } catch (IOException e) {
            logger.severe("Failed to export data to CSV: " + e.getMessage());
            throw new Exception("Failed to export data: " + e.getMessage());
        }
    }
    
    /**
     * Export data to PDF format (placeholder implementation)
     */
    public String exportDataToPDF() throws Exception {
        // For now, create a text file with PDF extension
        // In a real application, you would use a PDF library like iText
        String filename = "ecohabit_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
        
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("EcoHabit User Report\n");
            writer.write("===================\n\n");
            writer.write("Profile Information:\n");
            writer.write("Name: " + currentSettings.getFirstName() + " " + currentSettings.getLastName() + "\n");
            writer.write("Email: " + currentSettings.getEmail() + "\n");
            writer.write("Age: " + currentSettings.getAge() + "\n");
            writer.write("Location: " + currentSettings.getLocation() + "\n\n");
            
            writer.write("Preferences:\n");
            writer.write("Diet Type: " + currentSettings.getDietType() + "\n");
            writer.write("Transport: " + currentSettings.getTransportMethod() + "\n");
            writer.write("User Level: " + currentSettings.getUserType() + "\n");
            writer.write("Theme: " + currentSettings.getTheme() + "\n");
        }
        
        logger.info("Data exported to report: " + filename);
        return new File(filename).getAbsolutePath();
    }
    
    /**
     * Create backup of all data
     */
    public String createBackup() throws Exception {
        try {
            // Create backup directory if it doesn't exist
            File backupDir = new File(BACKUP_DIR);
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }
            
            String backupFilename = BACKUP_DIR + "/backup_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".properties";
            
            // Copy settings file to backup
            File originalFile = new File(SETTINGS_FILE);
            File backupFile = new File(backupFilename);
            
            if (originalFile.exists()) {
                try (FileInputStream fis = new FileInputStream(originalFile);
                     FileOutputStream fos = new FileOutputStream(backupFile)) {
                    
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                }
            }
            
            logger.info("Backup created: " + backupFilename);
            return backupFile.getAbsolutePath();
            
        } catch (IOException e) {
            logger.severe("Failed to create backup: " + e.getMessage());
            throw new Exception("Failed to create backup: " + e.getMessage());
        }
    }
    
    /**
     * Restore from backup (placeholder implementation)
     */
    public void restoreFromBackup() throws Exception {
        logger.info("Restore from backup functionality would be implemented here");
        // In a real application, you would show a file chooser to select backup file
        // and then restore the settings from that file
    }
    
    /**
     * Clear all data
     */
    public void clearAllData() throws Exception {
        try {
            // Delete settings file
            File settingsFile = new File(SETTINGS_FILE);
            if (settingsFile.exists()) {
                settingsFile.delete();
            }
            
            // Reset to default settings
            currentSettings = new UserSettings();
            properties = new Properties();
            
            logger.info("All data cleared successfully");
            
        } catch (Exception e) {
            logger.severe("Failed to clear data: " + e.getMessage());
            throw new Exception("Failed to clear data: " + e.getMessage());
        }
    }
    
    /**
     * Change user password (placeholder implementation)
     */
    public boolean changePassword(String currentPassword, String newPassword) throws Exception {
        // In a real application, you would verify the current password
        // and update it in a secure database
        logger.info("Password change functionality would be implemented here");
        return true; // Simulate success
    }
    
    /**
     * Delete user account (placeholder implementation)
     */
    public void deleteAccount() throws Exception {
        // In a real application, you would delete all user data from database
        clearAllData();
        logger.info("Account deletion functionality would be implemented here");
    }

	public UserSettings getUserSettings(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public UserSettings createDefaultSettings(User currentUser) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean saveUserProfile(User currentUser) {
		// TODO Auto-generated method stub
		return false;
	}
}