package com.vfortro.gestoreta.tasks;
import static org.mockito.Mockito.*;

import com.vfortro.gestoreta.model.enums.NotificationType;
import com.vfortro.gestoreta.model.inventory.Loan;
import com.vfortro.gestoreta.model.inventory.LoanContact;
import com.vfortro.gestoreta.model.inventory.LoanNotification;
import com.vfortro.gestoreta.repository.inventory.LoanNotificationRepository;
import com.vfortro.gestoreta.repository.inventory.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CheckLoanDateTest {

    @Mock private LoanRepository loanRepository;
    @Mock private LoanNotificationRepository notificationRepository;

    @InjectMocks
    private CheckLoanDate checkLoanDate;

    @Test
    void checkLoanDate_NoPendingLoans_ShouldOnlyCloseEndedLoans() {
        // Arrange
        when(loanRepository.findPendingLoansWithoutDelayNotifications()).thenReturn(Collections.emptyList());

        // Act
        checkLoanDate.checkLoanDate();

        // Assert
        verify(loanRepository, times(1)).closeEndedLoans();
        verify(notificationRepository, never()).saveAndFlush(any(LoanNotification.class));
    }

    @Test
    void checkLoanDate_WithPendingLoans_ShouldCreateAndSaveReminderNotifications() {
        // Arrange
        LoanContact mockContact = new LoanContact();

        Loan loan1 = new Loan();
        loan1.setContact(mockContact);

        Loan loan2 = new Loan();
        loan2.setContact(mockContact);

        when(loanRepository.findPendingLoansWithoutDelayNotifications()).thenReturn(List.of(loan1, loan2));

        // Act
        checkLoanDate.checkLoanDate();

        // Assert
        verify(loanRepository, times(1)).closeEndedLoans();

        // Verificamos que se guarden exactamente 2 notificaciones de tipo REMINDER
        verify(notificationRepository, times(2)).saveAndFlush(argThat(notification ->
                notification.getType() == NotificationType.REMINDER &&
                        notification.getSuccessful() &&
                        notification.getContact() == mockContact
        ));
    }
}