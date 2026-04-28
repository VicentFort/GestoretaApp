package com.vfortro.gestoreta.repository.payments;


import com.vfortro.gestoreta.model.payments.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon,Long> {
}
