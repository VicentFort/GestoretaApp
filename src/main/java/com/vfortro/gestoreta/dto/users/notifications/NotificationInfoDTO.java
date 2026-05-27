package com.vfortro.gestoreta.dto.users.notifications;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
public class NotificationInfoDTO {
    private Long id;
    private String message;
    private String type;
    private LocalDateTime date;
    private Boolean read;
    private Long userId;
    private String userName;

    private Long fallaId;
    private String falla;

    private Long eventId;
    private String event;

    private Long paymentId;
    private String paymentMessage;

    private Long couponId;
    private String coupon;
}
