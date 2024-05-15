package org.example.securityproject.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loginTokens")
public class LoginToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "expirationTime", nullable = false)
    private LocalDateTime expirationTime;

    @Column(name = "visited", nullable = false)
    private boolean visited;

    @Column(name = "hmac", nullable = false)
    private String hmac;


    public LoginToken() {}

    public LoginToken(Integer id, String token, LocalDateTime expirationTime, boolean visited, String hmac) {
        this.id = id;
        this.token = token;
        this.expirationTime = expirationTime;
        this.visited = visited;
        this.hmac = hmac;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token = token;
    }

    public LocalDateTime getExpirationTime(){
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime){
        this.expirationTime = expirationTime;
    }

    public boolean getVisited(){
        return visited;
    }

    public void setVisited(boolean visited){
        this.visited = visited;
    }

    public String getHmac() {
        return hmac;
    }

    public void setHmac(String hmac) {
        this.hmac = hmac;
    }
}
