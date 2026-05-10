package com.vfortro.gestoreta.model;

import com.vfortro.gestoreta.model.inventory.*;
import com.vfortro.gestoreta.model.payments.Coupon;
import com.vfortro.gestoreta.model.payments.CouponStock;
import com.vfortro.gestoreta.model.payments.Payment;
import com.vfortro.gestoreta.model.payments.Purchase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "fallas")
public class Falla implements Serializable {
    private static final long serialVersionUID = 6773890139169497651L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "falla_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @Column(name = "shield_url", length = Integer.MAX_VALUE)
    private String shieldUrl;

    @OneToMany(mappedBy = "falla")
    private Set<EventTag> eventTags = new LinkedHashSet<>();

    @OneToMany(mappedBy = "falla")
    private Set<Event> events = new LinkedHashSet<>();

    @OneToMany(mappedBy = "falla")
    private Set<Position> positions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "falla")
    private Set<Request> requests = new LinkedHashSet<>();

    @OneToMany(mappedBy = "falla")
    private Set<User> users = new LinkedHashSet<>();

    @OneToMany(mappedBy = "falla")
    private Set<Store> stores = new LinkedHashSet<>();

    @OneToMany(mappedBy = "falla")
    private Set<Loan> loans =  new LinkedHashSet<>();

    @OneToMany(mappedBy = "falla")
    private Set<InventoryItem> items = new LinkedHashSet<>();

    @OneToMany(mappedBy = "falla")
    private Set<LoanContact> contacts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "falla")
    private Set<InventoryMovement> movements = new LinkedHashSet<>();

    @OneToMany(mappedBy = "falla")
    private Set<Coupon> coupons = new LinkedHashSet<>();

    @OneToMany(mappedBy = "falla")
    private Set<CouponStock> couponStocks = new LinkedHashSet<>();

    @OneToMany(mappedBy = "falla")
    private Set<Purchase> purchases = new LinkedHashSet<>();

    @OneToMany(mappedBy = "falla")
    private Set<Payment> paymentLogs = new LinkedHashSet<>();

}