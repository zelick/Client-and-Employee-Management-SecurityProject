package org.example.securityproject.dto;

public class VerificationReCaptchaServerRequestDto {
    private String secret;
    private String response;

    public VerificationReCaptchaServerRequestDto(){}

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
