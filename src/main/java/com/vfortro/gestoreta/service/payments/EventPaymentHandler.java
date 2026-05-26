package com.vfortro.gestoreta.service.payments;

import com.vfortro.gestoreta.dto.payments.EventPaymentRequestDTO;
import com.vfortro.gestoreta.dto.payments.GenericPaymentRequestDTO;
import com.vfortro.gestoreta.model.Event;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.model.enums.PaymentType;
import com.vfortro.gestoreta.model.payments.Payment;
import com.vfortro.gestoreta.service.EventService;
import com.vfortro.gestoreta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class EventPaymentHandler implements PaymentHandler{

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Override
    public boolean supports(PaymentType type) {
        return type == PaymentType.EVENT_PAYMENT;
    }

    @Override
    public List<Payment> processPayment(GenericPaymentRequestDTO dto, User manager) throws AccessDeniedException, IllegalAccessException, IllegalStateException {
        EventPaymentRequestDTO request = (EventPaymentRequestDTO) dto;
        List<Payment> payments = new ArrayList();



        User user = userService.readUserAsEntity(request.getUserId());
        Event event = eventService.readEventAsEntity(request.getEventId());

        if(!Objects.equals(event.getFalla().getId(), manager.getFalla().getId())) {
            throw new IllegalAccessException("Sense permís.");
        }

        if(event.getPrice() <= 0) {
            throw new IllegalStateException("El esdeveniment no te preu a pagar.");
        }

        if(Math.abs(event.getPrice() - request.getPrice()) > 0.00001) {
            throw new IllegalStateException("El preu de l'esdeveniment no coincideix a la quantitat abonada.");
        }
        eventService.payAssist(user.getId(), event.getId());
        String message = "Pagament de l'esdeveniment: " + event.getTitle() + " per part de l'usuari: " + user.getName() + " " + user.getSurname() + " amb valor: " + event.getPrice() + " a data: " + event.getDate().truncatedTo(ChronoUnit.MINUTES);
        Payment payment = new Payment();
        payment.setType(PaymentType.EVENT_PAYMENT);
        payment.setManager(manager);
        payment.setFalla(manager.getFalla());
        payment.setUser(user);
        payment.setEvent(event);
        payment.setMessage(message);
        payment.setDate(LocalDateTime.now());
        payment.setPrice(request.getPrice());

        payments.add(payment);

        return payments;
    }
}
