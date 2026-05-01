package com.vfortro.gestoreta.conversor.payments;

import com.vfortro.gestoreta.dto.payments.CouponRequestDTO;
import com.vfortro.gestoreta.model.payments.Coupon;
import com.vfortro.gestoreta.model.payments.Purchase;
import com.vfortro.gestoreta.model.payments.PurchaseDetail;
import com.vfortro.gestoreta.repository.payments.CouponRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PurchaseDetailConversor {

    @Autowired
    private CouponRepository couponRepository;

    public PurchaseDetail fromDto2Entity(CouponRequestDTO dto, Purchase purchase, Coupon coupon) throws EntityNotFoundException {
        PurchaseDetail detail = new PurchaseDetail();
        detail.setAmount(dto.getAmount());
        detail.setPurchase(purchase);
        detail.setCoupon(coupon);
        return detail;
    }
}
