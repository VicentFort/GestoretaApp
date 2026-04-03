package com.vfortro.gestoreta.service.inventory;

import com.vfortro.gestoreta.conversor.InventoryItemConversor;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemCreateDto;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemInfoDto;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.inventory.InventoryItem;
import com.vfortro.gestoreta.repository.FallaRepository;
import com.vfortro.gestoreta.repository.inventory.InventoryItemRepository;
import com.vfortro.gestoreta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
public class InventoryItemService {
     @Autowired
    private InventoryItemRepository inventoryItemRepository;
     @Autowired
    private InventoryItemConversor inventoryItemConversor;
     @Autowired
    private UserService userService;
     @Autowired
    private FallaRepository fallaRepository;

     public InventoryItemInfoDto createItem(InventoryItemCreateDto newItem, String email) throws AccessDeniedException {
         if(!userService.checkAdminAccess(email)) { throw new AccessDeniedException("Sense permís");}
         Falla falla = fallaRepository.findFallaById(userService.readUser(email).getFallaId());
         InventoryItem toSave = inventoryItemConversor.fromDto2Entity(newItem, falla);
         InventoryItem saved = inventoryItemRepository.saveAndFlush(toSave);
         return inventoryItemConversor.fromEntity2Dto(saved);
     }
}
