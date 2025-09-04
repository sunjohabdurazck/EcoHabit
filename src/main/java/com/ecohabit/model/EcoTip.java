package main.java.com.ecohabit.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Model class representing an eco-friendly tip
 */
public class EcoTip {
    private int id;
    private String title;
    private String description;
    private String category;
    private String difficulty;
    private String icon;
    private double rating;
    private int readCount;
    private int likeCount;
    private LocalDate dateCreated;
    private LocalDateTime lastUpdated;
    private double estimatedCO2Savings;
    private String source;
    private boolean isFeatured;
    private String[] tags;
    private String imageUrl;
    private int userId; // Creator of the tip
    private boolean isApproved;

    // Constructors
    public EcoTip() {
        this.dateCreated = LocalDate.now();
        this.lastUpdated = LocalDateTime.now();
        this.rating = 0.0;
        this.readCount = 0;
        this.likeCount = 0;
        this.isApproved = true;
    }

    public EcoTip(String title, String description, String category, String difficulty) {
        this();
        this.title = title;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
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

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = Math.max(0.0, Math.min(5.0, rating)); // Clamp between 0 and 5
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = Math.max(0, readCount);
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = Math.max(0, likeCount);
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public double getEstimatedCO2Savings() {
        return estimatedCO2Savings;
    }

    public void setEstimatedCO2Savings(double estimatedCO2Savings) {
        this.estimatedCO2Savings = Math.max(0.0, estimatedCO2Savings);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    // Utility methods
    public void incrementReadCount() {
        this.readCount++;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public String getDifficultyColor() {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return "#4CAF50"; // Green
            case "medium":
                return "#FF9800"; // Orange
            case "hard":
                return "#F44336"; // Red
            default:
                return "#9E9E9E"; // Gray
        }
    }

    public String getCategoryColor() {
        switch (category.toLowerCase()) {
            case "energy":
                return "#FFC107"; // Amber
            case "transportation":
                return "#2196F3"; // Blue
            case "food":
                return "#4CAF50"; // Green
            case "water":
                return "#00BCD4"; // Cyan
            case "waste":
                return "#795548"; // Brown
            case "home":
                return "#9C27B0"; // Purple
            case "shopping":
                return "#E91E63"; // Pink
            default:
                return "#607D8B"; // Blue Gray
        }
    }

    public String getFormattedRating() {
        return String.format("%.1f", rating);
    }

    public String getShortDescription() {
        if (description == null) return "";
        return description.length() > 100 ? description.substring(0, 97) + "..." : description;
    }

    public boolean hasTag(String tag) {
        if (tags == null || tag == null) return false;
        for (String t : tags) {
            if (t != null && t.equalsIgnoreCase(tag.trim())) {
                return true;
            }
        }
        return false;
    }

    public void addTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) return;
        
        if (tags == null) {
            tags = new String[]{tag.trim()};
        } else {
            // Check if tag already exists
            if (!hasTag(tag)) {
                String[] newTags = new String[tags.length + 1];
                System.arraycopy(tags, 0, newTags, 0, tags.length);
                newTags[tags.length] = tag.trim();
                tags = newTags;
            }
        }
    }

    public void updateRating(double newRating, int totalRatings) {
        if (totalRatings <= 0) return;
        
        // Simple average calculation (in real app, you'd track individual ratings)
        this.rating = ((this.rating * (totalRatings - 1)) + newRating) / totalRatings;
    }

    public boolean isPopular() {
        return readCount >= 100 || likeCount >= 50 || rating >= 4.5;
    }

    public boolean isRecent() {
        return dateCreated != null && dateCreated.isAfter(LocalDate.now().minusDays(7));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EcoTip ecoTip = (EcoTip) o;
        return id == ecoTip.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EcoTip{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", rating=" + rating +
                ", readCount=" + readCount +
                '}';
    }
}