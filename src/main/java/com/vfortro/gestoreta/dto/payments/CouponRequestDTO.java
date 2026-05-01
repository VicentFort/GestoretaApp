package com.vfortro.gestoreta.dto.payments;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class CouponRequestDTO {
    private Long couponId;
    private Long amount;
}
