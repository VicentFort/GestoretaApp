package com.vfortro.gestoreta.service.payments;

import com.vfortro.gestoreta.dto.payments.GenericPaymentRequestDTO;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.model.enums.PaymentType;
import com.vfortro.gestoreta.model.payments.Payment;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface PaymentHandler {
    boolean supports(PaymentType type);
    List<Payment> processPayment(GenericPaymentRequestDTO dto, User manager) throws AccessDeniedException, IllegalAccessException;
}
