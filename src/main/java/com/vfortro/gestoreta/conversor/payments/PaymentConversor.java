package com.vfortro.gestoreta.conversor.payments;

import com.vfortro.gestoreta.dto.payments.info.PaymentInfoDTO;
import com.vfortro.gestoreta.model.payments.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentConversor {

    public PaymentInfoDTO fromEntity2Dto(Payment payment) {
        PaymentInfoDTO dto = new PaymentInfoDTO();
        dto.setId(payment.getPaymentId());
        dto.setPrice(payment.getPrice());
        dto.setDisplayPrice(payment.getPrice() + "€");
        dto.setDate(payment.getDate());
        dto.setMessage(payment.getMessage());
        dto.setManager(payment.getManager().getName() + " " + payment.getManager().getSurname());
        dto.setFalla(payment.getFalla().getName());
        dto.setType(payment.getType().getValue().replace('.', '\''));
        if(payment.getCouponExchanged() != null) {
            dto.setCouponExchanged(payment.getCouponExchanged().getName());
        }
        if(payment.getCouponSold() != null) {
            dto.setCouponSold(payment.getCouponSold().getName());
        }
        if(payment.getUser() != null) {
            dto.setUsername(payment.getUser().getName() + " " + payment.getUser().getSurname());
        }
        if(payment.getItem() != null) {
            dto.setItem(payment.getItem().getName());
        }
        if(payment.getEvent() != null) {
            dto.setEvent((payment.getEvent().getTitle()));
        }
        return dto;
    }
}
