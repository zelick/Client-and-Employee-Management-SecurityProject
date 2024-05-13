package org.example.securityproject.repository;

import org.example.securityproject.model.LoginToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginTokenRepository extends JpaRepository<LoginToken, Integer> {
    LoginToken findByToken(String token);
}
