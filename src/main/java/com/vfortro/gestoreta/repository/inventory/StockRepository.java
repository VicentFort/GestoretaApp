package com.vfortro.gestoreta.repository.inventory;

import com.vfortro.gestoreta.model.inventory.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByStoreStoreIdAndInventoryItemItemId(Long storeId, Long itemId);
}
