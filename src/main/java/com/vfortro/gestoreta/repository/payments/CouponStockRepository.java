package com.vfortro.gestoreta.repository.payments;

import com.vfortro.gestoreta.model.payments.CouponStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponStockRepository extends JpaRepository<CouponStock, Long> {
    Optional<CouponStock> findByCouponCouponIdAndUserId(Long couponId, Long userId);
}
