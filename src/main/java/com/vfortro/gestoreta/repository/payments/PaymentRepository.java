package com.vfortro.gestoreta.repository.payments;

import com.vfortro.gestoreta.model.payments.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
