package com.vfortro.gestoreta.dto.payments;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Data
public class ExchangeRequestDto {
    @NotNull
    private List<CouponRequestDto> coupons;
    @NotNull
    private Long userId;
    @NotNull
    private Long storeId;

}
