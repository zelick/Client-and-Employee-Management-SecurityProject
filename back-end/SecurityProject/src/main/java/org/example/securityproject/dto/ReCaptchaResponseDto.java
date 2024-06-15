package org.example.securityproject.dto;
import java.time.LocalDateTime;

public class ReCaptchaResponseDto {
    private boolean success;
    private LocalDateTime challenge_ts;
    private String hostname;
    private String error_codes;

    public ReCaptchaResponseDto() {}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public LocalDateTime getChallenge_ts() {
        return challenge_ts;
    }

    public void setChallenge_ts(LocalDateTime challenge_ts) {
        this.challenge_ts = challenge_ts;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getError_codes() {
        return error_codes;
    }

    public void setError_codes(String error_codes) {
        this.error_codes = error_codes;
    }
}
