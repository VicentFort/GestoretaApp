package com.vfortro.gestoreta.repository.inventory;

import com.vfortro.gestoreta.model.inventory.LoanContact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanContactRepository extends JpaRepository<LoanContact, Long> {
}
