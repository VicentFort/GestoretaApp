package com.vfortro.gestoreta.dto.payments;

import com.vfortro.gestoreta.model.enums.PaymentLogType;
import lombok.Data;

@Data
public abstract class GenericPaymentRequestDTO {
    private PaymentLogType type;
    private Long managerId;
    private Long fallaId;
}