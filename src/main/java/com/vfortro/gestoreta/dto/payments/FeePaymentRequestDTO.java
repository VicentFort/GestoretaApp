package com.vfortro.gestoreta.dto.payments;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
public class FeePaymentRequestDTO extends GenericPaymentRequestDTO{
    @NotNull
    private Long userId;
    @NotNull
    private Double feeAmount;
}
