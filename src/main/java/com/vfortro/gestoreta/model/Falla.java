package com.vfortro.gestoreta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "fallas")
public class Falla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "falla_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @Column(name = "shield_url", length = Integer.MAX_VALUE)
    private String shieldUrl;

    @OneToMany(mappedBy = "falla")
    private Set<EventTag> eventTags = new LinkedHashSet<>();

    @OneToMany(mappedBy = "falla")
    private Set<Event> events = new LinkedHashSet<>();

    @OneToMany(mappedBy = "falla")
    private Set<Request> requests = new LinkedHashSet<>();

    @OneToMany(mappedBy = "falla")
    private Set<User> users = new LinkedHashSet<>();


}