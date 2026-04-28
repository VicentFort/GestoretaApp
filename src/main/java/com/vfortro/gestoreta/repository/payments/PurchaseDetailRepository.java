package com.vfortro.gestoreta.repository.payments;

import com.vfortro.gestoreta.model.payments.PurchaseDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseDetailRepository extends JpaRepository<PurchaseDetail, Long> {
}
