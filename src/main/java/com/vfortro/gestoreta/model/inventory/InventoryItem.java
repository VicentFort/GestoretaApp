package com.vfortro.gestoreta.model.inventory;

import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.enums.ItemCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "inventory_item")
public class InventoryItem implements Serializable {
    @Id
    @Column(name = "item_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @NotNull
    @Column(name="name", nullable = false)
    private String name;

    @NotNull
    @Column(name="description", nullable = false)
    private String description;

    @NotNull
    @Column(name="category", nullable = false, columnDefinition = "item_category")
    private ItemCategory itemCategory;
    

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "falla_id", nullable = false)
    private Falla falla;

    @OneToMany(mappedBy = "inventoryItem")
    private Set<Stock> stocks = new LinkedHashSet<>();

    @OneToMany(mappedBy = "item")
    private Set<Loan> loans = new LinkedHashSet<>();

    @OneToMany(mappedBy = "item")
    private Set<InventoryMovement> movements = new LinkedHashSet<>();

}
