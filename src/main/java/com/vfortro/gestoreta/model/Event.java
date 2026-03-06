package com.vfortro.gestoreta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event implements Serializable {
    private static final long serialVersionUID = 3954095671962870245L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "falla", nullable = false)
    private Falla falla;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "event_tag", nullable = false)
    private EventTag eventTag;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "public", nullable = false)
    private Boolean publicField;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "done", nullable = false)
    private Boolean done;

    @Column(name = "price")
    private Float price;

    @NotNull
    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "max_people")
    private Integer maxPeople;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "title", nullable = false, length = Integer.MAX_VALUE)
    private String title;


    @Column(name = "start_hour")
    private LocalTime startHour;

    @Column(name = "end_hour")
    private LocalTime endHour;

    @OneToMany(mappedBy = "event")
    private Set<Assist> assists = new LinkedHashSet<>();

    @OneToMany(mappedBy = "event")
    private Set<Attendant> attendants = new LinkedHashSet<>();

    @NotNull
    @Column(name="created_by", nullable = false)
    private String creatdBy;


}