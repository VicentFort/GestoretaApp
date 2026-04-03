package com.vfortro.gestoreta.model.inventory;

import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.enums.LoanState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "loan")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Long loanId;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Long amount;

    @NotNull
    @Column(name = "acquisition_date", nullable = false)
    private LocalDateTime acquisitionDate;

    @NotNull
    @Column(name = "ideal_return_date", nullable = false)
    private LocalDateTime idealReturnDate;

    @Column(name = "real_return_date", nullable = true)
    private LocalDateTime realReturnDate;

    @NotNull
    @Column(name = "state", nullable = false, columnDefinition = "loan_state")
    private LoanState state;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "falla_id", nullable = false)
    private Falla falla;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contact_id", nullable = false)
    private LoanContact contact;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    @OneToMany(mappedBy = "loan")
    private Set<LoanNotification> notifications = new LinkedHashSet<>();

    @OneToMany(mappedBy = "loan")
    private Set<InventoryMovement> movements = new LinkedHashSet<>();
}
