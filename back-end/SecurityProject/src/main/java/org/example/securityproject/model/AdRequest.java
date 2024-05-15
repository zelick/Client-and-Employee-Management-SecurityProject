package org.example.securityproject.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "ad_requests")
public class AdRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "deadline", nullable = false)
    private Date deadline;

    @Column(name = "active_from", nullable = false)
    private Date activeFrom;

    @Column(name = "active_to", nullable = false)
    private Date activeTo;

    @Column(name = "description", nullable = false)
    private String description;

    public AdRequest(String email, Date deadline, Date activeFrom, Date activeTo, String description) {
        this.email = email;
        this.deadline = deadline;
        this.activeFrom = activeFrom;
        this.activeTo = activeTo;
        this.description = description;
    }

    public AdRequest() {

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
