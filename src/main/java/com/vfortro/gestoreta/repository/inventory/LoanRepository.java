package com.vfortro.gestoreta.repository.inventory;

import com.vfortro.gestoreta.model.inventory.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
