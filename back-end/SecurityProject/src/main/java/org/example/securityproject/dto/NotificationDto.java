package org.example.securityproject.dto;

import java.time.LocalDateTime;

public class NotificationDto {

    private Integer id;
    private String message;
    private LocalDateTime createdAt;

    public NotificationDto() {
    }

    public NotificationDto(Integer id, String message, LocalDateTime createdAt) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
