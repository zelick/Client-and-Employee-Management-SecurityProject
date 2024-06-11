package org.example.securityproject.dto;

public class VerificationReCaptchaRequestDto {
    private String reCaptchaToken;

    public VerificationReCaptchaRequestDto() {}
    public String getReCaptchaToken() {
        return reCaptchaToken;
    }

    public void setReCaptchaToken(String reCaptchaToken) {
        this.reCaptchaToken = reCaptchaToken;
    }
}
