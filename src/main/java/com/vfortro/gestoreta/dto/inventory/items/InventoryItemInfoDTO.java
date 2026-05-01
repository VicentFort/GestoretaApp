package com.vfortro.gestoreta.dto.inventory.items;

import com.vfortro.gestoreta.dto.inventory.stocks.StockInfoDTO;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Data
public class InventoryItemInfoDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private Boolean enabled;
    private List<StockInfoDTO> stocks;
}
