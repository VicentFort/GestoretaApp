package com.vfortro.gestoreta.repository;

import com.vfortro.gestoreta.model.Falla;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FallaRepository extends JpaRepository<Falla, Long> {
    Falla findByName(String name);

    Falla findFallaById(Long id);
}
