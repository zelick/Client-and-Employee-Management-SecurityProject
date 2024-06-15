package org.example.securityproject.dto;

import org.example.securityproject.enums.ClientType;
import org.example.securityproject.enums.RegistrationStatus;
import org.example.securityproject.enums.ServicesPackage;
import org.example.securityproject.enums.UserRole;
import org.example.securityproject.model.User;

import java.util.List;

public class UserDto {
    private String email;
    private String password;
    private String name;
    private String surname;
    private String address;
    private String city;
    private String country;
    private String phoneNumber;
    private List<UserRole> roles;
    private ClientType clientType;
    private ServicesPackage servicesPackage;
    private RegistrationStatus registrationStatus;
    private boolean mfaEnabled;

    public UserDto() {}

    public UserDto(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.clientType = user.getClientType();
        this.registrationStatus = user.getRegistrationStatus();
        this.address = user.getAddress();
        this.phoneNumber = user.getPhoneNumber();
        this.city = user.getCity();
        this.country = user.getCountry();
        this.roles = user.getRoles();
        this.clientType = user.getClientType();
        this.servicesPackage = user.getServicesPackage();
        this.mfaEnabled = user.isMfaEnabled();
    }

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType type) {
        this.clientType = type;
    }

    public ServicesPackage getServicesPackage() {
        return servicesPackage;
    }

    public void setServicesPackage(ServicesPackage servicesPackage) {
        this.servicesPackage = servicesPackage;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setMfaEnabled(boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }
}