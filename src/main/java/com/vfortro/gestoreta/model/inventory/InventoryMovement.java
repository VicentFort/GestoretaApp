package com.vfortro.gestoreta.model.inventory;

import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.enums.MovementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "inventory_movements")
public class InventoryMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movementId;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Long amount;

    @NotNull
    @Column(name = "date",  nullable = false)
    private LocalDateTime date;

    @NotNull
    @Column(name = "type", nullable = false, columnDefinition = "movement_type")
    private MovementType type;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="item_id", nullable = false)
    private InventoryItem item;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "loan_id", nullable = true)
    private Loan loan;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "falla_id", nullable = false)
    private Falla falla;


}
