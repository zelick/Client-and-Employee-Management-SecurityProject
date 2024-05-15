package org.example.securityproject.repository;

import org.example.securityproject.model.Ad;
import org.example.securityproject.model.AdRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdRepository extends JpaRepository<Ad, Integer> {
    List<Ad> findAllByUser_Email(String email);
}