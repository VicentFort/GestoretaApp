package com.vfortro.gestoreta.model;

import com.vfortro.gestoreta.model.payments.CouponStock;
import com.vfortro.gestoreta.model.payments.Payment;
import com.vfortro.gestoreta.model.payments.Purchase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @NotNull
    @Column(name = "surname", nullable = false, length = Integer.MAX_VALUE)
    private String surname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "falla")
    private Falla falla;

    @Column(name = "url_pfp", length = Integer.MAX_VALUE)
    private String urlPfp;

    @NotNull
    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "show_bday", nullable = false)
    private Boolean showBday;

    @NotNull
    @ColumnDefault("'1234'")
    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    private String password;

    @Column(name = "email", length = Integer.MAX_VALUE)
    private String email;

    @OneToMany(mappedBy = "user")
    private Set<Assist> assists = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<AttendantPreference> attendantPreferences = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Attendant> attendants = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<FoodNeed> foodNeeds = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Charge> charges = new LinkedHashSet<>();

    @OneToMany
    private Set<Request> requests = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<CouponStock> stocks = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Purchase> purchases = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Payment> paymentLogs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "manager")
    private Set<Payment> managerLogs = new LinkedHashSet<>();

    @Column(name="nickname")
    private String nickname;

    @NotNull
    @Column(name="creation_date", nullable = false)
    @ColumnDefault("now()")
    private LocalDateTime creationDate;

    @Column(name="join_date")
    private LocalDate joinDate;

}