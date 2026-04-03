package com.vfortro.gestoreta.repository.inventory;

import com.vfortro.gestoreta.model.inventory.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
