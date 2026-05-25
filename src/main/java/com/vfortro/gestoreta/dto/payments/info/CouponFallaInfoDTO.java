package com.vfortro.gestoreta.dto.payments.info;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class CouponFallaInfoDTO {
    private Long id;
    private String name;
    private Double price;
    private String item;
    private Long itemId;
    private Long totalAmount;
}
