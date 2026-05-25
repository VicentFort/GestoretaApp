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
    private List<CouponRequestDTO> coupons;
    @NotNull
    private String userEmail;
    @NotNull
    private Long storeId;

}
