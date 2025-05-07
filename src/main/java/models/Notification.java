package models;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private int receiverId;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;

    public Long getVlogId() {
        return vlogId;
    }

    public Notification(int id, int receiverId, String message, LocalDateTime createdAt, boolean isRead, Long vlogId) {
        this.id = id;
        this.receiverId = receiverId;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.vlogId = vlogId;
    }

    public void setVlogId(Long vlogId) {
        this.vlogId = vlogId;
    }

    private Long vlogId;  // Ajoutez un identifiant de vlog


    public Notification() {
    }

    public Notification(int id, int receiverId, String message, LocalDateTime createdAt, boolean isRead) {
        this.id = id;
        this.receiverId = receiverId;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
}
