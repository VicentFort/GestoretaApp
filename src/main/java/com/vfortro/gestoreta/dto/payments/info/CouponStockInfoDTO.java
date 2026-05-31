package com.vfortro.gestoreta.dto.payments.info;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class CouponStockInfoDTO {
    private Long id;
    private Long amount;
    private Long couponId;
    private Long itemId;
    private Long fallaId;
    private String falla;
    private String coupon;
}
