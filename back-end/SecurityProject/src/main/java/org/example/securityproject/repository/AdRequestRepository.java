package org.example.securityproject.repository;

import org.example.securityproject.model.AdRequest;
import org.example.securityproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdRequestRepository extends JpaRepository<AdRequest, Integer> {
    void deleteByEmail(String email);
    List<AdRequest> findAll();
    void delete(AdRequest adRequest);
}
