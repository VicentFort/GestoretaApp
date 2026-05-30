package com.vfortro.gestoreta.dto.payments;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data

public class CouponExchangeRequestDTO extends GenericPaymentRequestDTO{
    @NotNull
    private CouponRequestDTO coupon;
    @NotNull
    private Long stockId;
    @NotNull
    private Long storeId;

}
