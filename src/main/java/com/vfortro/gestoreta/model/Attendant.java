package com.vfortro.gestoreta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "attendants")
public class Attendant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User users;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Event events;

    @Column(name = "charge_description", length = Integer.MAX_VALUE)
    private String chargeDescription;


}