package com.vfortro.gestoreta.repository.inventory;

import com.vfortro.gestoreta.model.inventory.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE loans SET state = 'Atrassat' where ideal_return_date < CURRENT_TIMESTAMP AND state != 'Tornat'", nativeQuery = true)
    void closeEndedLoans();

    @Query(value = "SELECT l FROM Loan l " +
            "WHERE l.state = com.vfortro.gestoreta.model.enums.LoanState.DELAYED " +
            "AND NOT EXISTS ( SELECT n FROM l.notifications n WHERE n.type = com.vfortro.gestoreta.model.enums.NotificationType.DELAY)")
    List<Loan> findPendingLoansWithoutDelayNotifications();
}
