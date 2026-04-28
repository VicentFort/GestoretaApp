package com.vfortro.gestoreta.repository.payments;

import com.vfortro.gestoreta.model.payments.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {
}
