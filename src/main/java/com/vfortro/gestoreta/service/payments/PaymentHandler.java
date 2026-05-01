package com.vfortro.gestoreta.service.payments;

import com.vfortro.gestoreta.dto.payments.GenericPaymentRequestDTO;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.model.enums.PaymentLogType;
import com.vfortro.gestoreta.model.payments.PaymentLog;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface PaymentHandler {
    boolean supports(PaymentLogType type);
    List<PaymentLog> processPayment(GenericPaymentRequestDTO dto, User manager) throws AccessDeniedException;
}
