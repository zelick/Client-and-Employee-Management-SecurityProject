package org.example.securityproject.dto;

import java.util.Date;

public class AdDto {
    private UserDto user;
    private Date activeFrom;
    private Date activeTo;
    private String description;
    private String slogan;

    public AdDto(UserDto user, Date activeFrom, Date activeTo, String description, String slogan) {
        this.user = user;
        this.activeFrom = activeFrom;
        this.activeTo = activeTo;
        this.description = description;
        this.slogan = slogan;
    }

    public AdDto() {

    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
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

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }
}
