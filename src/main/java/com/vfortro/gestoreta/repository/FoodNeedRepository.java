package com.vfortro.gestoreta.repository;

import com.vfortro.gestoreta.model.FoodNeed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodNeedRepository extends JpaRepository<FoodNeed, Long> {
}
