package com.vfortro.gestoreta.dto.payments;

import com.vfortro.gestoreta.model.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public abstract class GenericPaymentRequestDTO {
    @NotNull
    private PaymentType type;
}