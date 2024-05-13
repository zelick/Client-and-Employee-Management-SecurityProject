package org.example.securityproject.dto;

public class RegistrationRequestResponseDto {
    private String email;
    private boolean accepted;
    private String reason;

    public RegistrationRequestResponseDto() {}

    public RegistrationRequestResponseDto(String email, boolean accepted, String reason) {
        this.email = email;
        this.accepted = accepted;
        this.reason = reason;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
