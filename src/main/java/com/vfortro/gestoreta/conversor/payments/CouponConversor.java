package com.vfortro.gestoreta.conversor.payments;

import com.vfortro.gestoreta.dto.payments.coupons.CouponCreateDTO;
import com.vfortro.gestoreta.dto.payments.info.CouponFallaInfoDTO;
import com.vfortro.gestoreta.dto.payments.info.CouponStockInfoDTO;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.inventory.InventoryItem;
import com.vfortro.gestoreta.model.payments.Coupon;
import com.vfortro.gestoreta.model.payments.CouponStock;
import com.vfortro.gestoreta.repository.FallaRepository;
import com.vfortro.gestoreta.repository.inventory.InventoryItemRepository;
import com.vfortro.gestoreta.service.QrCodeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class CouponConversor{
    @Autowired
    private FallaRepository fallaRepository;

    @Autowired
    private InventoryItemRepository itemRepository;


    public Coupon fromDto2Entity(CouponCreateDTO dto) throws EntityNotFoundException {
        Coupon coupon = new Coupon();
        coupon.setName(dto.getName());
        coupon.setPrice(dto.getPrice());
        Falla falla = fallaRepository.findById(dto.getFallaId()).orElseThrow(() -> new EntityNotFoundException("No existeix la falla amb id: " + dto.getFallaId()));
        coupon.setFalla(falla);
        InventoryItem item = itemRepository.findById(dto.getItemId()).orElseThrow(() -> new EntityNotFoundException("No existeix el item amb id: " + dto.getItemId()));
        coupon.setItem(item);
        return coupon;
    }

    public CouponFallaInfoDTO fromEntity2Dto(Coupon coupon) {
        CouponFallaInfoDTO dto = new CouponFallaInfoDTO();
        dto.setId(coupon.getCouponId());
        dto.setName(coupon.getName());
        dto.setPrice(coupon.getPrice());
        InventoryItem item = coupon.getItem();
        dto.setItem(item.getName());
        dto.setItemId(item.getItemId());
        AtomicReference<Long> count = new AtomicReference<>(0L);
        item.getStocks().forEach(stock -> {
            count.updateAndGet(v -> v + stock.getAmount());
        });
        dto.setTotalAmount(count.get());
        return dto;
    }

    public CouponStockInfoDTO fromEntity2Dto(CouponStock stock) {
        CouponStockInfoDTO dto = new CouponStockInfoDTO();
        dto.setId(stock.getStockId());
        dto.setAmount(stock.getAmount());
        dto.setCouponId(stock.getCoupon().getCouponId());
        dto.setCoupon(stock.getCoupon().getName());
        dto.setItemId(stock.getCoupon().getItem().getItemId());
        dto.setFallaId(stock.getCoupon().getFalla().getId());
        dto.setFalla(stock.getCoupon().getFalla().getName());
        return dto;
    }
}
