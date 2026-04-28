package com.vfortro.gestoreta.repository.payments;

import com.vfortro.gestoreta.model.payments.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
