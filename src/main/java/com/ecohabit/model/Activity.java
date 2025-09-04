package main.java.com.ecohabit.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Model class representing an eco-friendly activity
 */
public class Activity {
    private int id;
    private String description;
    private LocalDate date;
    private double co2Saved;
    private String category;
    private String activityType;
    private int userId; // To link activity to a specific user
    private boolean isCompleted;
    private String notes;
    private double points; // Points earned for this activity
    private double quantity;  // Add this field
    private String unit;      // Add this field
    private boolean completed;
    /**
     * Default constructor
     */
    
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Activity() {
        this.date = LocalDate.now();
        this.isCompleted = false;
        this.points = 0.0;
    }
    
    /**
     * Constructor with basic information
     */
    public Activity(String description, LocalDate date, double co2Saved) {
        this();
        this.description = description;
        this.date = date;
        this.co2Saved = co2Saved;
    }
    
    /**
     * Constructor with category and type
     */
    public Activity(String description, LocalDate date, double co2Saved, 
                   String category, String activityType) {
        this(description, date, co2Saved);
        this.category = category;
        this.activityType = activityType;
    }
    
    /**
     * Full constructor
     */
    public Activity(int id, String description, LocalDate date, double co2Saved,
                   String category, String activityType, int userId) {
        this(description, date, co2Saved, category, activityType);
        this.id = id;
        this.userId = userId;
    }
    
    // Getters and Setters
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public LocalDate getDate() { 
        return date; 
    }
    
    public void setDate(LocalDate date) { 
        this.date = date; 
    }
    
    public double getCo2Saved() { 
        return co2Saved; 
    }
    
    public void setCo2Saved(double co2Saved) { 
        this.co2Saved = co2Saved;
        // Calculate points based on CO2 saved (1 point per kg CO2 saved)
        this.points = co2Saved;
    }
    
    public String getCategory() { 
        return category; 
    }
    
    public void setCategory(String category) { 
        this.category = category; 
    }
    
    public String getActivityType() { 
        return activityType; 
    }
    
    public void setActivityType(String activityType) { 
        this.activityType = activityType; 
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public double getPoints() {
        return points;
    }
    
    public void setPoints(double points) {
        this.points = points;
    }
    
    // Utility Methods
    
    /**
     * Get formatted date string
     */
    public String getFormattedDate() {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
    
    /**
     * Get formatted date string with custom pattern
     */
    public String getFormattedDate(String pattern) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * Check if activity is from today
     */
    public boolean isToday() {
        return date != null && date.equals(LocalDate.now());
    }
    
    /**
     * Check if activity is from this week
     */
    public boolean isThisWeek() {
        if (date == null) return false;
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1);
        return !date.isBefore(startOfWeek) && !date.isAfter(now);
    }
    
    /**
     * Check if activity is from this month
     */
    public boolean isThisMonth() {
        if (date == null) return false;
        LocalDate now = LocalDate.now();
        return date.getYear() == now.getYear() && date.getMonth() == now.getMonth();
    }
    
    /**
     * Get CO2 saved formatted as string with unit
     */
    public String getFormattedCO2Saved() {
        if (co2Saved < 1) {
            return String.format("%.0f g CO₂", co2Saved * 1000);
        } else {
            return String.format("%.2f kg CO₂", co2Saved);
        }
    }
    
    /**
     * Get points formatted as string
     */
    public String getFormattedPoints() {
        return String.format("%.0f pts", points);
    }
    
    /**
     * Get activity summary for display
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(description);
        if (category != null && !category.isEmpty()) {
            summary.append(" (").append(category).append(")");
        }
        summary.append(" - ").append(getFormattedCO2Saved());
        return summary.toString();
    }
    
    /**
     * Mark activity as completed
     */
    public void markCompleted() {
        this.isCompleted = true;
    }
    
    /**
     * Mark activity as incomplete
     */
    public void markIncomplete() {
        this.isCompleted = false;
    }
    
    /**
     * Calculate impact level based on CO2 saved
     */
    public String getImpactLevel() {
        if (co2Saved >= 10) {
            return "High Impact";
        } else if (co2Saved >= 5) {
            return "Medium Impact";
        } else if (co2Saved >= 1) {
            return "Low Impact";
        } else {
            return "Minimal Impact";
        }
    }
    
    /**
     * Get days since activity
     */
    public long getDaysAgo() {
        if (date == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(date, LocalDate.now());
    }
    
    /**
     * Check if activity is recent (within last 7 days)
     */
    public boolean isRecent() {
        return getDaysAgo() <= 7;
    }
    
    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", co2Saved=" + co2Saved +
                ", category='" + category + '\'' +
                ", activityType='" + activityType + '\'' +
                ", userId=" + userId +
                ", isCompleted=" + isCompleted +
                ", points=" + points +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Activity activity = (Activity) obj;
        return id == activity.id;
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}