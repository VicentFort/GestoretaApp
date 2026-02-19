package com.vfortro.gestoreta.model;

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
@Table(name = "event_tags")
public class EventTag implements Serializable {
    private static final long serialVersionUID = 4663231415681494303L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_tag_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "falla", nullable = false)
    private Falla falla;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @OneToMany(mappedBy = "eventTag")
    private Set<AttendantPreference> attendantPreferences = new LinkedHashSet<>();

    @OneToMany(mappedBy = "eventTag")
    private Set<Event> events = new LinkedHashSet<>();


}