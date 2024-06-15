package org.example.securityproject.dto;

public class EmailDto {
    private String email;
    public String getEmail() { return email;}

    public void setEmail(String email) {
        this.email = email;
    }

    public EmailDto() { }
    public EmailDto(String email) { this.email = email; }
}
