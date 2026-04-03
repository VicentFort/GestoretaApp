package com.vfortro.gestoreta.repository.inventory;

import com.vfortro.gestoreta.model.inventory.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
}
