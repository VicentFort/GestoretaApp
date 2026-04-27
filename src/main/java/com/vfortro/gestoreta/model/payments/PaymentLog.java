package com.vfortro.gestoreta.model.payments;

import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.model.enums.PaymentLogType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payment_logs")
public class PaymentLog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", nullable = false)
    private Long logId;

    @Column(name = "price")
    private Float price;

    @NotNull()
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "message")
    private String message;

    @NotNull
    @Column(name="type", nullable = false, columnDefinition = "payment_log_type")
    private PaymentLogType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "falla_id", nullable = false)
    private Falla falla;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

}
