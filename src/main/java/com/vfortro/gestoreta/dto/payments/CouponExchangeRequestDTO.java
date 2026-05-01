package com.vfortro.gestoreta.dto.payments;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data

public class CouponExchangeRequestDTO extends GenericPaymentRequestDTO{
    @NotNull
    private List<CouponRequestDto> coupons;
    @NotNull
    private Long userId;
    @NotNull
    private Long storeId;

}
