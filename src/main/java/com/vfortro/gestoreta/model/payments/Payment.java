package com.vfortro.gestoreta.model.payments;

import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.model.enums.PaymentType;
import com.vfortro.gestoreta.model.inventory.InventoryItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", nullable = false)
    private Long paymentId;

    @Column(name = "price")
    private Double price;

    @NotNull()
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "message")
    private String message;

    @NotNull
    @Column(name="type", nullable = false, columnDefinition = "payment_log_type")
    private PaymentType type;

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
    @JoinColumn(name = "coupon_exchanged_id")
    private Coupon couponExchanged;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_sold_id")
    private Coupon couponSold;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private InventoryItem item;


}
