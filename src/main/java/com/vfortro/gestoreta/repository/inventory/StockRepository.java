package com.vfortro.gestoreta.repository.inventory;

import com.vfortro.gestoreta.model.inventory.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}
