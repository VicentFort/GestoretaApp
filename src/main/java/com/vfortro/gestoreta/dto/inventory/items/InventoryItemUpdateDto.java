package com.vfortro.gestoreta.dto.inventory.items;

import com.vfortro.gestoreta.model.enums.ItemCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryItemUpdateDto {
    private Long itemId;
    private String name;
    private String description;
    private ItemCategory itemCategory;
    private Boolean enabled;
}
