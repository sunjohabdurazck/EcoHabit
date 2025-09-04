package main.java.com.ecohabit.service;

import main.java.com.ecohabit.model.UserSettings;

import java.util.logging.Logger;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Service class for managing notifications and reminders
 */
public class NotificationService {
    
    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());
    
    private ScheduledExecutorService scheduler;
    private UserSettings notificationSettings;
    
    public NotificationService() {
        this.scheduler = Executors.newScheduledThreadPool(2);
        logger.info("NotificationService initialized");
    }
    
    /**
     * Update notification settings
     */
    public void updateNotificationSettings(UserSettings settings) {
        this.notificationSettings = settings;
        
        // Cancel existing scheduled tasks
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        
        // Recreate scheduler with new settings
        scheduler = Executors.newScheduledThreadPool(2);
        
        // Schedule notifications based on settings
        if (settings.isDailyReminder()) {
            scheduleDailyReminder();
        }
        
        if (settings.isWeeklySummary()) {
            scheduleWeeklySummary();
        }
        
        logger.info("Notification settings updated: " + settings.toString());
    }
    
    /**
     * Schedule daily reminder notifications
     */
    private void scheduleDailyReminder() {
        scheduler.scheduleAtFixedRate(() -> {
            if (notificationSettings != null && notificationSettings.isDailyReminder()) {
                showNotification(
                    "Daily Reminder",
                    "Don't forget to log your eco-friendly activities today! 🌱",
                    NotificationType.REMINDER
                );
            }
        }, 0, 24, TimeUnit.HOURS);
        
        logger.info("Daily reminder notifications scheduled");
    }
    
    /**
     * Schedule weekly summary notifications
     */
    private void scheduleWeeklySummary() {
        scheduler.scheduleAtFixedRate(() -> {
            if (notificationSettings != null && notificationSettings.isWeeklySummary()) {
                showNotification(
                    "Weekly Summary",
                    "Check out your weekly environmental impact summary! 📊",
                    NotificationType.SUMMARY
                );
            }
        }, 7, 7, TimeUnit.DAYS);
        
        logger.info("Weekly summary notifications scheduled");
    }
    
    /**
     * Send achievement notification
     */
    public void sendAchievementNotification(String achievement, String description) {
        if (notificationSettings != null && notificationSettings.isAchievementNotifications()) {
            showNotification(
                "Achievement Unlocked! 🏆",
                achievement + ": " + description,
                NotificationType.ACHIEVEMENT
            );
        }
    }
    
    /**
     * Send eco tip notification
     */
    public void sendEcoTip(String tip) {
        if (notificationSettings != null && notificationSettings.isEcoTips()) {
            showNotification(
                "💡 Eco Tip",
                tip,
                NotificationType.TIP
            );
        }
    }
    
    /**
     * Show notification (placeholder implementation)
     */
    private void showNotification(String title, String message, NotificationType type) {
        // In a real application, you would show actual system notifications
        // or update the UI notification area
        
        String sound = notificationSettings != null ? notificationSettings.getNotificationSound() : "Default";
        
        logger.info(String.format("NOTIFICATION [%s] [%s]: %s - %s", 
            type, sound, title, message));
        
        // Here you could integrate with:
        // - System notifications (Windows, macOS, Linux)
        // - In-app notification components
        // - Sound playback based on notification sound setting
        
        playNotificationSound(sound);
    }
    
    /**
     * Play notification sound (placeholder implementation)
     */
    private void playNotificationSound(String soundType) {
        // In a real application, you would play actual sound files
        logger.info("Playing notification sound: " + soundType);
        
        // Example implementation would use JavaFX Media API:
        // MediaPlayer mediaPlayer = new MediaPlayer(new Media(soundFilePath));
        // mediaPlayer.play();
    }
    
    /**
     * Send custom notification
     */
    public void sendCustomNotification(String title, String message) {
        showNotification(title, message, NotificationType.CUSTOM);
    }
    
    /**
     * Test notification system
     */
    public void sendTestNotification() {
        showNotification(
            "Test Notification",
            "EcoHabit notification system is working! ✅",
            NotificationType.TEST
        );
    }
    
    /**
     * Shutdown the notification service
     */
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        logger.info("NotificationService shutdown");
    }
    
    /**
     * Check if notifications are enabled
     */
    public boolean areNotificationsEnabled() {
        return notificationSettings != null && (
            notificationSettings.isDailyReminder() ||
            notificationSettings.isWeeklySummary() ||
            notificationSettings.isAchievementNotifications() ||
            notificationSettings.isEcoTips()
        );
    }
    
    /**
     * Get notification statistics
     */
    public String getNotificationStats() {
        if (notificationSettings == null) {
            return "No notification settings configured";
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append("Notification Settings Status:\n");
        stats.append("Daily Reminders: ").append(notificationSettings.isDailyReminder() ? "Enabled" : "Disabled").append("\n");
        stats.append("Weekly Summaries: ").append(notificationSettings.isWeeklySummary() ? "Enabled" : "Disabled").append("\n");
        stats.append("Achievements: ").append(notificationSettings.isAchievementNotifications() ? "Enabled" : "Disabled").append("\n");
        stats.append("Eco Tips: ").append(notificationSettings.isEcoTips() ? "Enabled" : "Disabled").append("\n");
        stats.append("Sound: ").append(notificationSettings.getNotificationSound());
        
        return stats.toString();
    }
    
    /**
     * Enum for notification types
     */
    public enum NotificationType {
        REMINDER,
        SUMMARY,
        ACHIEVEMENT,
        TIP,
        CUSTOM,
        TEST
    }
}