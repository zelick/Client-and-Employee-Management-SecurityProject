package org.example.securityproject.model;

//import jakarta.persistence.*;
import org.example.securityproject.enums.RegistrationStatus;
import org.example.securityproject.enums.ServicesPackage;
import org.example.securityproject.enums.ClientType;
import org.example.securityproject.enums.UserRole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.sql.Timestamp;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;


import java.util.Date;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "salt", nullable = false)
    private String salt;

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "surname", nullable = true)
    private String surname;

    @Column(name = "address", nullable = true)
    private String address;

    @Column(name = "city", nullable = true)
    private String city;

    @Column(name = "country", nullable = true)
    private String country;

    @Column(name = "phoneNumber", nullable = true)
    private String phoneNumber;

    @Column(name = "blocked", nullable = false)
    private boolean blocked;

    /*
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;
     */

    //tabela se zvala user_roles
    //provera da li ovo moze da se menja iz koda
    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<UserRole> roles;

    /*
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRoleEntity> roles = new ArrayList<>();

     */

    @Column(name = "clientType", nullable = false)
    @Enumerated(EnumType.STRING)
    private ClientType clientType;

    @Column(name = "servicesPackage", nullable = false)
    @Enumerated(EnumType.STRING)
    private ServicesPackage servicesPackage;

    @Column(name = "registrationStatus", nullable = false)
    @Enumerated(EnumType.STRING)
    private RegistrationStatus registrationStatus;

    @Column(name = "last_password_reset_date")
    private Timestamp lastPasswordResetDate;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "requestProcessingDate", nullable = true)
    private Date requestProcessingDate;

    @Column(name = "loggedInOnce", nullable = false)
    private boolean loggedInOnce;

    public User() {}

    public User(Integer id, Date requestProcessingDate, boolean loggedInOnce, boolean active, boolean enabled, Timestamp lastPasswordResetDate, RegistrationStatus registrationStatus, ServicesPackage servicesPackage, ClientType clientType, List<UserRole> roles, String phoneNumber, String country, String address, String city, String surname, String salt, String name, String password, String email) {
        this.id = id;
        this.requestProcessingDate = requestProcessingDate;
        this.loggedInOnce = loggedInOnce;
        this.active = active;
        this.enabled = enabled;
        this.lastPasswordResetDate = lastPasswordResetDate;
        this.registrationStatus = registrationStatus;
        this.servicesPackage = servicesPackage;
        this.clientType = clientType;
        this.roles = roles;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.address = address;
        this.city = city;
        this.surname = surname;
        this.salt = salt;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public User(Integer id, Date requestProcessingDate, boolean loggedInOnce, boolean active, boolean enabled, Timestamp lastPasswordResetDate, RegistrationStatus registrationStatus, ServicesPackage servicesPackage, ClientType clientType, List<UserRole> roles, String phoneNumber, String country, String address, String city, String surname, String salt, String name, String password, String email, boolean blocked) {
        this.id = id;
        this.requestProcessingDate = requestProcessingDate;
        this.loggedInOnce = loggedInOnce;
        this.active = active;
        this.enabled = enabled;
        this.lastPasswordResetDate = lastPasswordResetDate;
        this.registrationStatus = registrationStatus;
        this.servicesPackage = servicesPackage;
        this.clientType = clientType;
        this.roles = roles;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.address = address;
        this.city = city;
        this.surname = surname;
        this.salt = salt;
        this.name = name;
        this.password = password;
        this.email = email;
        this.blocked = blocked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        Timestamp now = new Timestamp(new Date().getTime());
        this.setLastPasswordResetDate(now);
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public ServicesPackage getServicesPackage() {
        return servicesPackage;
    }

    public void setServicesPackage(ServicesPackage servicesPackage) {
        this.servicesPackage = servicesPackage;
    }

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }


    /*
    !!!NAPOMENA:
    - ovo sam zakomentarisala i napravila novo za permisije, ako bude problema ispravicemo
    //jwt
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Napravi set ovlašćenja (roles) za korisnika
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(this.role.toString()));

        // Vrati ovlašćenja
        return authorities;
    }
     */

    /*
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

     */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        roles.forEach(role -> authorities.addAll(role.getAuthorities()));
        return authorities;
    }

    public Timestamp getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(Timestamp lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //UserDetails trazi da se implemenitra sta je username
    public String getUsername() {
        return this.email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public Date getRequestProcessingDate() {
        return requestProcessingDate;
    }

    public void setRequestProcessingDate(Date requestProcessingDate) {
        this.requestProcessingDate = requestProcessingDate;
    }

    public boolean isLoggedInOnce() {
        return loggedInOnce;
    }

    public void setLoggedInOnce(boolean loggedInOnce) {
        this.loggedInOnce = loggedInOnce;
    }
}
