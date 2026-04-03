package com.vfortro.gestoreta.dto.inventory.stocks;

import com.vfortro.gestoreta.conversor.InventoryItemConversor;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemInfoDto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Data
public class StockInfoDto {
    private Long id;
    private Long amount;
    private InventoryItemInfoDto item;
}
