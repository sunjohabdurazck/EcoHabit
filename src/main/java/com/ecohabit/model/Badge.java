package main.java.com.ecohabit.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Model class representing a user badge/achievement in the EcoHabit system
 */
public class Badge {
    private int id;
    private String title;
    private String description;
    private String category;
    private String icon;
    private String difficulty;
    private String rarity;
    private int points;
    private boolean earned;
    private LocalDateTime earnedDate;
    private LocalDateTime createdDate;
    private int progress; // 0-100 percentage
    private int currentValue;
    private int targetValue;
    private String criteria; // JSON string describing earning criteria
    private boolean isActive;
    private String imageUrl;
    private String[] tags;
    private int userId; // User who earned this badge
    private String badgeType; // "streak", "milestone", "achievement", "special"
    private LocalDateTime expiryDate; // For time-limited badges
    private boolean isPublic; // Can be shared/displayed publicly
    private String unlockCondition; // Human-readable unlock condition
    private int sortOrder; // Display order priority

    // Constructors
    public Badge() {
        this.createdDate = LocalDateTime.now();
        this.earned = false;
        this.progress = 0;
        this.isActive = true;
        this.isPublic = true;
        this.points = 0;
        this.difficulty = "Easy";
        this.rarity = "Common";
        this.badgeType = "achievement";
    }

    public Badge(String title, String description, String category, String difficulty) {
        this();
        this.title = title;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.rarity = mapDifficultyToRarity(difficulty);
        this.points = mapDifficultyToPoints(difficulty);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        // Auto-update rarity and points based on difficulty
        this.rarity = mapDifficultyToRarity(difficulty);
        this.points = mapDifficultyToPoints(difficulty);
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = Math.max(0, points);
    }

    public boolean isEarned() {
        return earned;
    }

    public void setEarned(boolean earned) {
        this.earned = earned;
        if (earned && earnedDate == null) {
            this.earnedDate = LocalDateTime.now();
            this.progress = 100;
        } else if (!earned) {
            this.earnedDate = null;
        }
    }

    public LocalDateTime getEarnedDate() {
        return earnedDate;
    }

    public void setEarnedDate(LocalDateTime earnedDate) {
        this.earnedDate = earnedDate;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(100, progress));
        if (this.progress == 100 && !earned) {
            setEarned(true);
        }
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = Math.max(0, currentValue);
        updateProgress();
    }

    public int getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(int targetValue) {
        this.targetValue = Math.max(1, targetValue);
        updateProgress();
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(String badgeType) {
        this.badgeType = badgeType;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getUnlockCondition() {
        return unlockCondition;
    }

    public void setUnlockCondition(String unlockCondition) {
        this.unlockCondition = unlockCondition;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    // Utility Methods
    
    /**
     * Map difficulty to rarity
     */
    private String mapDifficultyToRarity(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return "Common";
            case "medium":
                return "Uncommon";
            case "hard":
                return "Rare";
            case "very hard":
                return "Legendary";
            default:
                return "Common";
        }
    }

    /**
     * Map difficulty to points
     */
    private int mapDifficultyToPoints(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return 10;
            case "medium":
                return 25;
            case "hard":
                return 50;
            case "very hard":
                return 100;
            default:
                return 10;
        }
    }

    /**
     * Update progress based on current and target values
     */
    private void updateProgress() {
        if (targetValue <= 0) {
            progress = 0;
            return;
        }
        
        int newProgress = Math.min(100, (currentValue * 100) / targetValue);
        setProgress(newProgress);
    }

    /**
     * Increment current value
     */
    public void incrementCurrentValue(int amount) {
        setCurrentValue(currentValue + amount);
    }

    /**
     * Check if badge is expired
     */
    public boolean isExpired() {
        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }

    /**
     * Check if badge is newly earned (within last 24 hours)
     */
    public boolean isNewlyEarned() {
        return earned && earnedDate != null && 
               earnedDate.isAfter(LocalDateTime.now().minusHours(24));
    }

    /**
     * Check if badge is in progress (started but not completed)
     */
    public boolean isInProgress() {
        return !earned && progress > 0 && progress < 100;
    }

    /**
     * Check if badge is locked (no progress)
     */
    public boolean isLocked() {
        return !earned && progress == 0;
    }

    /**
     * Get progress as decimal (0.0 - 1.0)
     */
    public double getProgressDecimal() {
        return progress / 100.0;
    }

    /**
     * Get remaining value to complete badge
     */
    public int getRemainingValue() {
        return Math.max(0, targetValue - currentValue);
    }

    /**
     * Get formatted earned date
     */
    public String getFormattedEarnedDate() {
        if (earnedDate == null) return "Not earned";
        return earnedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    /**
     * Get formatted earned date with time
     */
    public String getFormattedEarnedDateTime() {
        if (earnedDate == null) return "Not earned";
        return earnedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }

    /**
     * Get progress description
     */
    public String getProgressDescription() {
        if (earned) return "Completed";
        if (progress == 0) return "Not started";
        return String.format("%d%% complete (%d/%d)", progress, currentValue, targetValue);
    }

    /**
     * Get rarity color for UI display
     */
    public String getRarityColor() {
        switch (rarity.toLowerCase()) {
            case "common":
                return "#808080"; // Gray
            case "uncommon":
                return "#4CAF50"; // Green
            case "rare":
                return "#2196F3"; // Blue
            case "epic":
                return "#9C27B0"; // Purple
            case "legendary":
                return "#FF9800"; // Orange/Gold
            default:
                return "#808080";
        }
    }

    /**
     * Get difficulty color for UI display
     */
    public String getDifficultyColor() {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return "#4CAF50"; // Green
            case "medium":
                return "#FF9800"; // Orange
            case "hard":
                return "#F44336"; // Red
            case "very hard":
                return "#9C27B0"; // Purple
            default:
                return "#808080"; // Gray
        }
    }

    /**
     * Get category color for UI display
     */
    public String getCategoryColor() {
        switch (category.toLowerCase()) {
            case "getting started":
                return "#4CAF50"; // Green
            case "environmental impact":
                return "#2196F3"; // Blue
            case "streak":
                return "#FF5722"; // Deep Orange
            case "transportation":
                return "#607D8B"; // Blue Gray
            case "waste reduction":
                return "#795548"; // Brown
            case "energy conservation":
                return "#FFC107"; // Amber
            case "food & garden":
                return "#8BC34A"; // Light Green
            case "water conservation":
                return "#00BCD4"; // Cyan
            case "social impact":
                return "#E91E63"; // Pink
            default:
                return "#9E9E9E"; // Gray
        }
    }

    /**
     * Check if badge has specific tag
     */
    public boolean hasTag(String tag) {
        if (tags == null || tag == null) return false;
        for (String t : tags) {
            if (t != null && t.equalsIgnoreCase(tag.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add tag to badge
     */
    public void addTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) return;
        
        if (tags == null) {
            tags = new String[]{tag.trim()};
        } else {
            if (!hasTag(tag)) {
                String[] newTags = new String[tags.length + 1];
                System.arraycopy(tags, 0, newTags, 0, tags.length);
                newTags[tags.length] = tag.trim();
                tags = newTags;
            }
        }
    }

    /**
     * Get status description for display
     */
    public String getStatusDescription() {
        if (earned) {
            return "Earned on " + getFormattedEarnedDate();
        } else if (isInProgress()) {
            return getProgressDescription();
        } else {
            return unlockCondition != null ? unlockCondition : "Complete activities to unlock";
        }
    }

    /**
     * Get time until expiry
     */
    public String getTimeUntilExpiry() {
        if (expiryDate == null) return "Never expires";
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiryDate)) return "Expired";
        
        java.time.Duration duration = java.time.Duration.between(now, expiryDate);
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        
        if (days > 0) {
            return days + " days, " + hours + " hours";
        } else {
            return hours + " hours";
        }
    }

    /**
     * Create a copy of this badge
     */
    public Badge copy() {
        Badge copy = new Badge();
        copy.setId(this.id);
        copy.setTitle(this.title);
        copy.setDescription(this.description);
        copy.setCategory(this.category);
        copy.setIcon(this.icon);
        copy.setDifficulty(this.difficulty);
        copy.setRarity(this.rarity);
        copy.setPoints(this.points);
        copy.setEarned(this.earned);
        copy.setEarnedDate(this.earnedDate);
        copy.setCreatedDate(this.createdDate);
        copy.setProgress(this.progress);
        copy.setCurrentValue(this.currentValue);
        copy.setTargetValue(this.targetValue);
        copy.setCriteria(this.criteria);
        copy.setActive(this.isActive);
        copy.setImageUrl(this.imageUrl);
        copy.setTags(this.tags != null ? this.tags.clone() : null);
        copy.setUserId(this.userId);
        copy.setBadgeType(this.badgeType);
        copy.setExpiryDate(this.expiryDate);
        copy.setPublic(this.isPublic);
        copy.setUnlockCondition(this.unlockCondition);
        copy.setSortOrder(this.sortOrder);
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Badge badge = (Badge) o;
        return id == badge.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Badge{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", rarity='" + rarity + '\'' +
                ", points=" + points +
                ", earned=" + earned +
                ", progress=" + progress +
                '}';
    }
}