package com.vfortro.gestoreta.model.inventory;

import com.vfortro.gestoreta.model.Falla;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "loan_contact")
public class LoanContact {
    @Id
    @Column(name="contact_id", nullable=false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contactId;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Column(name = "phone_number", nullable = false)
    private String phone;

    @NotNull
    @Column(name = "dni_cif", nullable = false)
    private String dniCif;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "falla_id", nullable = false)
    private Falla falla;

    @OneToMany(mappedBy = "contact")
    private Set<Loan> loans = new LinkedHashSet<>();

    @OneToMany(mappedBy = "contact")
    private Set<LoanNotification> notifications = new LinkedHashSet<>();
}

