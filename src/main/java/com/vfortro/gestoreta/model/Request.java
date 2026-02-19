package com.vfortro.gestoreta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "requests")
public class Request implements Serializable {
    private static final long serialVersionUID = -3952361813637721698L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "falla_id", nullable = false)
    private Falla falla;

    @NotNull
    @Column(name = "message", nullable = false, length = Integer.MAX_VALUE)
    private String message;

    @Column(name = "aproved")
    private Boolean aproved;

    @Column(name = "reply", length = Integer.MAX_VALUE)
    private String reply;


}