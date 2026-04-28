package com.vfortro.gestoreta.dto.payments.coupons;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class CouponCreateDto {
    private Long fallaId;
    private Long itemId;
    private Double price;
    private String name;
}

