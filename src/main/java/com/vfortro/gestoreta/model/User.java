package com.vfortro.gestoreta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
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

    @OneToMany(mappedBy = "user")
    private Set<Assist> assists = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<FoodNeed> foodNeeds = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Request> requests = new LinkedHashSet<>();

    @ManyToMany
    private Set<Position> positions = new LinkedHashSet<>();


}