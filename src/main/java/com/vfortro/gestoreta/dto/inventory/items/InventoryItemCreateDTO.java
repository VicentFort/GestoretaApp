package com.vfortro.gestoreta.dto.inventory.items;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@NoArgsConstructor
@Data
public class InventoryItemCreateDTO {
    private String name;
    private String description;
    private String category;
}
