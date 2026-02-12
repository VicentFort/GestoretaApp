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

    @NotNull
    @ColumnDefault("'1234'")
    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    private String password;

    @Column(name = "email", length = Integer.MAX_VALUE)
    private String email;

    @OneToMany
    @JoinColumn(name = "user_id")
    private Set<Assist> assists = new LinkedHashSet<>();

    @OneToMany
    @JoinColumn(name = "user_id")
    private Set<FoodNeed> foodNeeds = new LinkedHashSet<>();

    @OneToMany
    @JoinColumn(name = "user_id")
    private Set<Request> requests = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "user_positions", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "position_id")})
    private Set<Position> positions = new LinkedHashSet<>();


}