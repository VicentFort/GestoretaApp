package com.vfortro.gestoreta.tasks;

import com.vfortro.gestoreta.repository.inventory.LoanRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CheckLoanDate {

    @Autowired
    private LoanRepository loanRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkLoanDate() {
        loanRepository.closeEndedLoans();
        System.out.println("Prèstecs actualitzats");
    }
}
