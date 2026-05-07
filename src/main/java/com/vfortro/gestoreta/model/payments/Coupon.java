package com.vfortro.gestoreta.model.payments;

import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.inventory.InventoryItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "coupons")
public class Coupon implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @NotNull
    @Column(name = "price", nullable = false)
    private Double price;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "falla_id", nullable = false)
    private Falla falla;

    @OneToMany(mappedBy = "coupon")
    private Set<CouponStock> stocks = new LinkedHashSet<>();

    @OneToMany(mappedBy = "coupon")
    private Set<PurchaseDetail> purchaseDetails = new LinkedHashSet<>();

    @OneToMany(mappedBy = "coupon")
    private Set<Payment> paymentLogs = new LinkedHashSet<>();


}
