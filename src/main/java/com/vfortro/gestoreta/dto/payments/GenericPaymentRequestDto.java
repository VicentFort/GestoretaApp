package com.vfortro.gestoreta.dto.payments;

import com.vfortro.gestoreta.model.enums.PaymentLogType;
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
public class GenericPaymentRequestDto{
    @NotNull
    private PaymentLogType type;
    @NotNull
    private Long managerId;
    @NotNull
    private Long fallaId;

    private Long userId;

    private Long storeId;

    private Long itemId;

    private Long eventId;

    private Double price;

    private List<CouponRequestDto> coupons;


}
