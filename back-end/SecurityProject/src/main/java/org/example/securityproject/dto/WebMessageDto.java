package org.example.securityproject.dto;

public class WebMessageDto {

    private String message;

    public WebMessageDto() {
    }

    public WebMessageDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
