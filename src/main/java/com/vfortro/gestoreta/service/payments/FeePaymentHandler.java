package com.vfortro.gestoreta.service.payments;

import com.vfortro.gestoreta.dto.payments.FeePaymentRequestDTO;
import com.vfortro.gestoreta.dto.payments.GenericPaymentRequestDTO;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.model.enums.PaymentType;
import com.vfortro.gestoreta.model.payments.Payment;
import com.vfortro.gestoreta.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class FeePaymentHandler implements  PaymentHandler{

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(PaymentType type) {
        return type == PaymentType.FEE_PAYMENT;
    }

    @Override
    public List<Payment> processPayment(GenericPaymentRequestDTO dto, User manager) throws AccessDeniedException, EntityNotFoundException, IllegalAccessException {
        FeePaymentRequestDTO request = (FeePaymentRequestDTO) dto;

        User user = userService.readUserAsEntity(request.getUserId());
        if(user.getFalla() == null) throw new IllegalAccessException("L'usuari no te falla");
        if(!Objects.equals(user.getFalla().getId(), manager.getFalla().getId())) throw new IllegalAccessException("L'usuari no pertany a la falla del gestor");
        LocalDateTime now = LocalDateTime.now();
        String message = "";
        if(request.getMessage() == null || request.getMessage().isBlank()) {
            message = "Pagament de la quota de faller a la falla: " + manager.getFalla().getName() + " del / la membre: " + user.getName() + " " + user.getSurname() + " amb un import de: " + request.getFeeAmount() + " i amb data: " + now;
        } else {
            message = request.getMessage();
        }
        Payment payment = new Payment();
        payment.setManager(manager);
        payment.setFalla(manager.getFalla());
        payment.setPrice(request.getFeeAmount());
        payment.setType(PaymentType.FEE_PAYMENT);
        payment.setDate(now);
        payment.setMessage(message);
        payment.setUser(user);

        return List.of(payment);
    }
}
