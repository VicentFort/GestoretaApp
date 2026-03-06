package com.vfortro.gestoreta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "attendants")
public class Attendant implements Serializable {
    private static final long serialVersionUID = 8241776802089946975L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User users;

    @NotNull
    @JoinColumn(name = "event_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Event event;

    @NotNull
    @JoinColumn(name = "falla_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Falla falla;

    @Column(name = "charge_description", length = Integer.MAX_VALUE)
    private String chargeDescription;


}