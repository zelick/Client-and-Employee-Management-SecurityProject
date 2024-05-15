package org.example.securityproject.dto;

public class UserLoginData {
    private String email;
    private String password;

    public UserLoginData() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
