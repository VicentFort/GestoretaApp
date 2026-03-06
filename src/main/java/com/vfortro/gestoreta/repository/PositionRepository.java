package com.vfortro.gestoreta.repository;

import com.vfortro.gestoreta.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
    Position findPositionByUserId(Long userId);

    Position findByUserId(Long userId);
}
