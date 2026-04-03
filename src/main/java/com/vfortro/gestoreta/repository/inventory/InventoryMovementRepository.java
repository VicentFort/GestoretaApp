package com.vfortro.gestoreta.repository.inventory;

import com.vfortro.gestoreta.model.inventory.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
}
