package org.example.securityproject.dto;

import jakarta.persistence.Column;

import java.util.Date;

public class AdRequestDto {
    private String email;
    private Date deadline;
    private Date activeFrom;
    private Date activeTo;
    private String description;

    public AdRequestDto(String email, Date deadline, Date activeFrom, Date activeTo, String description) {
        this.email = email;
        this.deadline = deadline;
        this.activeFrom = activeFrom;
        this.activeTo = activeTo;
        this.description = description;
    }

    public AdRequestDto() {

    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(Date activeFrom) {
        this.activeFrom = activeFrom;
    }

    public Date getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(Date activeTo) {
        this.activeTo = activeTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
