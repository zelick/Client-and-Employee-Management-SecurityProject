package org.example.securityproject.dto;

import java.util.Date;

public class AdDto {
    private String email;
    private String name;
    private String surname;
    private Date activeFrom;
    private Date activeTo;
    private String description;
    private String slogan;

    public AdDto(String email, String name, String surname, Date activeFrom, Date activeTo, String description, String slogan) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.activeFrom = activeFrom;
        this.activeTo = activeTo;
        this.description = description;
        this.slogan = slogan;
    }

    public AdDto() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return email;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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
