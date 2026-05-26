package com.vfortro.gestoreta.dto.payments;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class EventPaymentRequestDTO extends GenericPaymentRequestDTO{
    @NotNull
    private Long eventId;

    @NotNull
    private Long userId;

    @NotNull
    private Double price;
}
