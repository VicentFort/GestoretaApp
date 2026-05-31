package com.vfortro.gestoreta.tasks;

import com.vfortro.gestoreta.model.enums.NotificationType;
import com.vfortro.gestoreta.model.inventory.Loan;
import com.vfortro.gestoreta.model.inventory.LoanNotification;
import com.vfortro.gestoreta.repository.inventory.LoanNotificationRepository;
import com.vfortro.gestoreta.repository.inventory.LoanRepository;
import com.vfortro.gestoreta.service.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CheckLoanDate {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private LoanNotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkLoanDate() {
        loanRepository.closeEndedLoans();
        List<Loan> pendingLoans =  loanRepository.findPendingLoansWithoutDelayNotifications();
        for(Loan loan : pendingLoans) {
            LoanNotification loanNotification = new LoanNotification();
            loanNotification.setLoan(loan);
            loanNotification.setType(NotificationType.REMINDER);
            loanNotification.setDate(LocalDateTime.now());
            loanNotification.setContact(loan.getContact());
            loanNotification.setSuccessful(true);
            String emailDestino = loan.getContact().getEmail();
            Long idPrestamo = loan.getLoanId();
            String nombreFalla = loan.getFalla().getName();

            emailService.sendLoanReminderMail(
                    emailDestino,
                    "Recordatori de préstec pendent",
                    idPrestamo,
                    nombreFalla
            );            notificationRepository.saveAndFlush(loanNotification);
        }
    }
}
