package com.vfortro.gestoreta.repository.inventory;

import com.vfortro.gestoreta.model.inventory.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
