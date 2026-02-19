package com.vfortro.gestoreta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "positions")
public class Position implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "falla_id", nullable = false)
    private Falla falla;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "admin_access", nullable = false)
    private Boolean adminAccess;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "bank_access", nullable = false)
    private Boolean bankAccess;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "lottery_access", nullable = false)
    private Boolean lotteryAccess;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "arts_access", nullable = false)
    private Boolean artsAccess;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "pyrotechnics_access", nullable = false)
    private Boolean pyrotechnicsAccess;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "household_access", nullable = false)
    private Boolean householdAccess;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "other_access", nullable = false)
    private Boolean otherAccess;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}