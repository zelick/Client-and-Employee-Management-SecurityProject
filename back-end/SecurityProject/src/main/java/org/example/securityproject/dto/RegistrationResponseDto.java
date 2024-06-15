package org.example.securityproject.dto;

public class RegistrationResponseDto {
    private String responseMessage;
    private boolean flag;
    private String secretImageUri;
    public RegistrationResponseDto() {

    }
    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getSecretImageUri() {
        return secretImageUri;
    }

    public void setSecretImageUri(String secretImageUri) {
        this.secretImageUri = secretImageUri;
    }
}
