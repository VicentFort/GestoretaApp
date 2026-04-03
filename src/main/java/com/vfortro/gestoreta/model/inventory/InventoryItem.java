package com.vfortro.gestoreta.model.inventory;

import com.vfortro.gestoreta.model.enums.ItemCategory;
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
    @Column(name="quantity", nullable = false, columnDefinition = "item_category")
    private ItemCategory itemCategory;

    @OneToMany(mappedBy = "inventoryItem")
    private Set<Stock> stocks = new LinkedHashSet<>();

    @OneToMany(mappedBy = "item")
    private Set<Loan> loans = new LinkedHashSet<>();

}
