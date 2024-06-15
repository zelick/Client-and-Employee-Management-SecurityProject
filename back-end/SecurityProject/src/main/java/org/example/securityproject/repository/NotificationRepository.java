package org.example.securityproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.securityproject.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer>{

}
