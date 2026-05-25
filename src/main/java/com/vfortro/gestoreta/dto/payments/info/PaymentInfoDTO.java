package com.vfortro.gestoreta.dto.payments.info;

import com.vfortro.gestoreta.model.enums.PaymentType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
public class PaymentInfoDTO {
    private Long id;
    private Double price;
    private String displayPrice;
    private LocalDateTime date;
    private String message;
    private String manager;
    private String falla;
    private String couponExchanged;
    private String couponSold;
    private String username;
    private String type;
    private String item;
}
