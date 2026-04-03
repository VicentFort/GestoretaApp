package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.inventory.items.InventoryItemCreateDto;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemInfoDto;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.enums.ItemCategory;
import com.vfortro.gestoreta.model.inventory.InventoryItem;
import org.springframework.stereotype.Service;

@Service
public class InventoryItemConversor {

    public InventoryItemInfoDto fromEntity2Dto(InventoryItem inventoryItem) {
        InventoryItemInfoDto dto = new InventoryItemInfoDto();
        dto.setId(inventoryItem.getItemId());
        dto.setDescription(inventoryItem.getDescription());
        dto.setName(inventoryItem.getName());
        dto.setCategory(inventoryItem.getItemCategory().getValue());
        return dto;
    }

    public InventoryItem fromDto2Entity(InventoryItemCreateDto dto, Falla falla) {
        InventoryItem item = new InventoryItem();
        item.setDescription(dto.getDescription());
        item.setName(dto.getName());
        item.setItemCategory(ItemCategory.fromString(dto.getCategory()));
        item.setFalla(falla);
        return item;
    }
}
