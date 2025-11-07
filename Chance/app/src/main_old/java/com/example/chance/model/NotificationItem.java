package com.example.chance.model;

/**
 * Represents a notification displayed to users.
 */
public class NotificationItem {
    private String id;
    private String title;
    private String message;
    private String timeAgo;

    public NotificationItem() {}

    public NotificationItem(String id, String title, String message, String timeAgo) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timeAgo = timeAgo;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTimeAgo() { return timeAgo; }
    public void setTimeAgo(String timeAgo) { this.timeAgo = timeAgo; }
}
