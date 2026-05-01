package com.vfortro.gestoreta.dto.payments;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class CouponPurchaseRequestDTO extends GenericPaymentRequestDTO {
    @NotNull
    private List<CouponRequestDTO> coupons;
    @NotNull
    private Long userId;
    @NotNull
    private Double totalPrice;
}
