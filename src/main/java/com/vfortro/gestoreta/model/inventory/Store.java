package com.vfortro.gestoreta.model.inventory;

import com.vfortro.gestoreta.model.Falla;
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
@Table(name = "store")
public class Store implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long storeId;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "location", nullable = false)
    private String location;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "falla_id", nullable = false)
    private Falla falla;

    @OneToMany(mappedBy = "store")
    private Set<Stock> stocks = new LinkedHashSet<>();

    @OneToMany(mappedBy = "store")
    private Set<InventoryMovement> movements = new LinkedHashSet<>();
}
