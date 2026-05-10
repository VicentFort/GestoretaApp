package com.vfortro.gestoreta.repository;

import com.vfortro.gestoreta.model.Charge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeRepository extends JpaRepository<Charge, Long> {
    Charge findPositionByUserId(Long userId);

    Charge findByUserId(Long userId);
}
