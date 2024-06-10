package org.example.securityproject.dto;

public class LoginReponseDto {
    private String response;
    private boolean loggedInOnce;
    private boolean mfaEnabled;
    public LoginReponseDto() {
    }
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isLoggedInOnce() {
        return loggedInOnce;
    }

    public void setLoggedInOnce(boolean loggedInOnce) {
        this.loggedInOnce = loggedInOnce;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    public void setMfaEnabled(boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }
}
