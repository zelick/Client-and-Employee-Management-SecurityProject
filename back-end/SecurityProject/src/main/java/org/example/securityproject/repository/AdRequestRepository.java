package org.example.securityproject.repository;

import org.example.securityproject.model.AdRequest;
import org.example.securityproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdRequestRepository extends JpaRepository<AdRequest, Integer> {
}
