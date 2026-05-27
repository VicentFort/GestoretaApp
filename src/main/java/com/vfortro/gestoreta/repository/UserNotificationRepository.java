package com.vfortro.gestoreta.repository;

import com.vfortro.gestoreta.model.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotificationRepository extends JpaRepository<UserNotification,Long> {
}
