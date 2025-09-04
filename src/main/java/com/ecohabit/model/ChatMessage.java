package main.java.com.ecohabit.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Model class representing a chat message in the EcoHabit chatbot system
 */
public class ChatMessage {
    private int id;
    private String content;
    private String sender;
    private LocalDateTime timestamp;
    private String messageType; // "user", "bot", "system"
    private String sessionId;
    private boolean isRead;
    private String attachmentUrl;
    private String messageCategory; // "question", "tip", "analysis", "general"
    private double confidence; // For bot responses - confidence level
    private String intent; // Detected user intent
    private String[] entities; // Extracted entities from the message
    private String contextData; // JSON string for additional context
    private boolean isFavorited;
    private String language;

    // Constructors
    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
        this.messageType = "user";
        this.isRead = false;
        this.isFavorited = false;
        this.language = "en";
        this.confidence = 1.0;
    }

    public ChatMessage(String content, String sender, String messageType) {
        this();
        this.content = content;
        this.sender = sender;
        this.messageType = messageType;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getMessageCategory() {
        return messageCategory;
    }

    public void setMessageCategory(String messageCategory) {
        this.messageCategory = messageCategory;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = Math.max(0.0, Math.min(1.0, confidence));
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String[] getEntities() {
        return entities;
    }

    public void setEntities(String[] entities) {
        this.entities = entities;
    }

    public String getContextData() {
        return contextData;
    }

    public void setContextData(String contextData) {
        this.contextData = contextData;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    // Utility Methods
    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }

    public String getTimeOnly() {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getDateOnly() {
        return timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    public boolean isFromUser() {
        return "user".equals(messageType);
    }

    public boolean isFromBot() {
        return "bot".equals(messageType);
    }

    public boolean isSystemMessage() {
        return "system".equals(messageType);
    }

    public boolean hasAttachment() {
        return attachmentUrl != null && !attachmentUrl.trim().isEmpty();
    }

    public boolean hasEntities() {
        return entities != null && entities.length > 0;
    }

    public boolean isHighConfidence() {
        return confidence >= 0.8;
    }

    public boolean isMediumConfidence() {
        return confidence >= 0.5 && confidence < 0.8;
    }

    public boolean isLowConfidence() {
        return confidence < 0.5;
    }

    public String getShortContent() {
        if (content == null) return "";
        return content.length() > 100 ? content.substring(0, 97) + "..." : content;
    }

    public int getWordCount() {
        if (content == null || content.trim().isEmpty()) return 0;
        return content.trim().split("\\s+").length;
    }

    public boolean isLongMessage() {
        return getWordCount() > 50;
    }

    public boolean isQuestion() {
        return content != null && content.trim().endsWith("?");
    }

    public boolean containsKeyword(String keyword) {
        if (content == null || keyword == null) return false;
        return content.toLowerCase().contains(keyword.toLowerCase());
    }

    public void addEntity(String entity) {
        if (entity == null || entity.trim().isEmpty()) return;
        
        if (entities == null) {
            entities = new String[]{entity.trim()};
        } else {
            // Check if entity already exists
            for (String existing : entities) {
                if (existing.equalsIgnoreCase(entity.trim())) {
                    return; // Already exists
                }
            }
            
            // Add new entity
            String[] newEntities = new String[entities.length + 1];
            System.arraycopy(entities, 0, newEntities, 0, entities.length);
            newEntities[entities.length] = entity.trim();
            entities = newEntities;
        }
    }

    public boolean hasEntity(String entity) {
        if (entities == null || entity == null) return false;
        
        for (String e : entities) {
            if (e != null && e.equalsIgnoreCase(entity.trim())) {
                return true;
            }
        }
        return false;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public void toggleFavorite() {
        this.isFavorited = !this.isFavorited;
    }

    public String getConfidenceLevel() {
        if (isHighConfidence()) return "High";
        if (isMediumConfidence()) return "Medium";
        return "Low";
    }

    public boolean isRecent() {
        return timestamp.isAfter(LocalDateTime.now().minusMinutes(30));
    }

    public boolean isToday() {
        return timestamp.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }

    public long getAgeInMinutes() {
        return java.time.Duration.between(timestamp, LocalDateTime.now()).toMinutes();
    }

    public String getRelativeTime() {
        long minutes = getAgeInMinutes();
        
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + "m ago";
        
        long hours = minutes / 60;
        if (hours < 24) return hours + "h ago";
        
        long days = hours / 24;
        if (days < 7) return days + "d ago";
        
        return getDateOnly();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", messageType='" + messageType + '\'' +
                ", timestamp=" + timestamp +
                ", content='" + getShortContent() + '\'' +
                ", confidence=" + confidence +
                '}';
    }

    /**
     * Create a copy of this message
     */
    public ChatMessage copy() {
        ChatMessage copy = new ChatMessage();
        copy.setId(this.id);
        copy.setContent(this.content);
        copy.setSender(this.sender);
        copy.setTimestamp(this.timestamp);
        copy.setMessageType(this.messageType);
        copy.setSessionId(this.sessionId);
        copy.setRead(this.isRead);
        copy.setAttachmentUrl(this.attachmentUrl);
        copy.setMessageCategory(this.messageCategory);
        copy.setConfidence(this.confidence);
        copy.setIntent(this.intent);
        copy.setEntities(this.entities != null ? this.entities.clone() : null);
        copy.setContextData(this.contextData);
        copy.setFavorited(this.isFavorited);
        copy.setLanguage(this.language);
        return copy;
    }
}