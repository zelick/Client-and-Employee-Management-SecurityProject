package org.example.securityproject.model;

import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name = "ads")
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "active_from", nullable = false)
    private Date activeFrom;

    @Column(name = "active_to", nullable = false)
    private Date activeTo;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "slogan", nullable = false)
    private String slogan;

    public Ad(User user, Date activeFrom, Date activeTo, String description, String slogan) {
        this.user = user;
        this.activeFrom = activeFrom;
        this.activeTo = activeTo;
        this.description = description;
        this.slogan = slogan;
    }

    public Ad() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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

    public void setSlogan(String slogan){
        this.slogan = slogan;
    }
}
