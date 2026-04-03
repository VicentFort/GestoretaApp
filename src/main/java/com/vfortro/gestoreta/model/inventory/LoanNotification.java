package com.vfortro.gestoreta.model.inventory;

import com.vfortro.gestoreta.model.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "loan_notification")
public class LoanNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @NotNull
    @Column(name = "successful", nullable = false)
    private Boolean successful;

    @NotNull
    @Column(name = "type", nullable = false, columnDefinition = "notification_type")
    private NotificationType type;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contact_id", nullable = false)
    private LoanContact contact;
}
