package com.vfortro.gestoreta.dto.payments;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Data
public class PurchaseRequestDto {
    private List<CouponRequestDto> coupons;
    private Long storeId;
    private Long userId;
    @NotNull
    private Double totalPrice;
}
