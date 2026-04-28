package com.vfortro.gestoreta.model.payments;

import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id", nullable = false)
    private Long purchaseId;

    @NotNull
    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "falla_id", nullable = false)
    private Falla falla;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
    private Set<PurchaseDetail> details = new LinkedHashSet<>();

    @OneToMany(mappedBy = "purchase")
    private Set<PaymentLog> paymentLogs = new LinkedHashSet<>();
}
