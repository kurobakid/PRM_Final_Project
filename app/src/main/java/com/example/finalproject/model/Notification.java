package com.example.finalproject.model;

import java.util.Date;

public class Notification {
    private String id;
    private String userId;
    private String title;
    private String message;
    private String type; // order_update, promotion, system, etc.
    private boolean isRead;
    private Date timestamp;
    private String actionUrl; // URL or deep link for notification action
    private String imageUrl;

    public Notification() {
        // Required empty constructor for Firebase
    }

    public Notification(String id, String title, String message, String type, Date timestamp, boolean isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    public Notification(String id, String userId, String title, String message, 
                       String type, boolean isRead, Date timestamp, 
                       String actionUrl, String imageUrl) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.timestamp = timestamp;
        this.actionUrl = actionUrl;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
} 