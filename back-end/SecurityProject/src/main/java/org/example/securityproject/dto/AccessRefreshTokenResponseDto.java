package org.example.securityproject.dto;

public class AccessRefreshTokenResponseDto {
    private String accessToken;
    private int accesExpiresIn;
    private String refreshToken;
    private int refreshExpiresIn;

    public AccessRefreshTokenResponseDto() {
    }

    public AccessRefreshTokenResponseDto(String accessToken, int refreshExpiresIn, String refreshToken, int accesExpiresIn) {
        this.accessToken = accessToken;
        this.refreshExpiresIn = refreshExpiresIn;
        this.refreshToken = refreshToken;
        this.accesExpiresIn = accesExpiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getRefreshExpiresIn() {
        return refreshExpiresIn;
    }

    public void setRefreshExpiresIn(int refreshExpiresIn) {
        this.refreshExpiresIn = refreshExpiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public int getAccesExpiresIn() {
        return accesExpiresIn;
    }

    public void setAccesExpiresIn(int accesExpiresIn) {
        this.accesExpiresIn = accesExpiresIn;
    }
}
