package com.vfortro.gestoreta.repository.inventory;

import com.vfortro.gestoreta.model.inventory.LoanNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanNotificationRepository extends JpaRepository<LoanNotification, Long> {
}
