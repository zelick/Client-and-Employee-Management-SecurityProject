package org.example.securityproject.repository;

import org.example.securityproject.enums.RegistrationStatus;
import org.example.securityproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByRegistrationStatus(RegistrationStatus registrationStatus);
    User findByEmail(String email);
    List<User> findAll();
    User findByEmailAndRegistrationStatusIn(String email, List<RegistrationStatus> statuses);
    User findByEmailAndRegistrationStatus(String email, RegistrationStatus status);
}
