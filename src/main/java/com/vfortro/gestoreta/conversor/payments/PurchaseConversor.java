package com.vfortro.gestoreta.conversor.payments;

import com.vfortro.gestoreta.dto.payments.CouponRequestDto;
import com.vfortro.gestoreta.dto.payments.PurchaseRequestDto;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.payments.Purchase;
import com.vfortro.gestoreta.repository.FallaRepository;
import com.vfortro.gestoreta.repository.UserRepository;
import com.vfortro.gestoreta.repository.payments.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PurchaseConversor {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    public Purchase fromDto2Entity(PurchaseRequestDto dto, Falla falla) {
        Purchase purchase = new Purchase();
        purchase.setUser(userRepository.findUserById(dto.getUserId()));
        purchase.setFalla(falla);
        purchase.setTotalPrice(dto.getTotalPrice());
        return purchase;
    }
}
