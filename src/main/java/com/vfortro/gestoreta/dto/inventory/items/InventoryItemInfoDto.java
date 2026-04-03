package com.vfortro.gestoreta.dto.inventory.items;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class InventoryItemInfoDto {
    private Long id;
    private String name;
    private String description;
    private String category;
}
