package com.vfortro.gestoreta.conversor.payments;

import com.vfortro.gestoreta.dto.payments.coupons.CouponCreateDto;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.inventory.InventoryItem;
import com.vfortro.gestoreta.model.payments.Coupon;
import com.vfortro.gestoreta.repository.FallaRepository;
import com.vfortro.gestoreta.repository.inventory.InventoryItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CouponConversor{
    @Autowired
    private FallaRepository fallaRepository;

    @Autowired
    private InventoryItemRepository itemRepository;

    public Coupon fromDto2Entity(CouponCreateDto dto) throws EntityNotFoundException {
        Coupon coupon = new Coupon();
        coupon.setName(dto.getName());
        coupon.setPrice(dto.getPrice());
        Falla falla = fallaRepository.findById(dto.getFallaId()).orElseThrow(() -> new EntityNotFoundException("No existeix la falla amb id: " + dto.getFallaId()));
        coupon.setFalla(falla);
        InventoryItem item = itemRepository.findById(dto.getItemId()).orElseThrow(() -> new EntityNotFoundException("No existeix el item amb id: " + dto.getItemId()));
        coupon.setItem(item);
        return coupon;
    }
}
