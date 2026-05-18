package com.vfortro.gestoreta.dto.payments.coupons;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class CouponEditDTO {
    @NotNull
    private Long couponId;
    private String name;
    private Double price;
    private Long itemId;
}
