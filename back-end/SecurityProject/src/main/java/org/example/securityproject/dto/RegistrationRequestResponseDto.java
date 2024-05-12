package org.example.securityproject.dto;

import org.example.securityproject.model.User;

public class RegistrationRequestResponseDto {
    private String email;
    private boolean isAccepted;
    private String reason;

    public RegistrationRequestResponseDto() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
