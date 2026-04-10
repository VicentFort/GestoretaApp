package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.inventory.items.InventoryItemCreateDto;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemInfoDto;
import com.vfortro.gestoreta.dto.inventory.stocks.StockInfoDto;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.enums.ItemCategory;
import com.vfortro.gestoreta.model.inventory.InventoryItem;
import com.vfortro.gestoreta.model.inventory.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryItemConversor {

    @Autowired
    private StockConversor stockConversor;

    public InventoryItemInfoDto fromEntity2Dto(InventoryItem inventoryItem) {
        InventoryItemInfoDto dto = new InventoryItemInfoDto();
        List<StockInfoDto> stocks = new ArrayList<>();
        dto.setId(inventoryItem.getItemId());
        dto.setDescription(inventoryItem.getDescription());
        dto.setName(inventoryItem.getName());
        dto.setCategory(inventoryItem.getItemCategory().getValue());
        dto.setEnabled(inventoryItem.getEnabled());
        for(Stock stock : inventoryItem.getStocks()){
            stocks.add(stockConversor.fromEntity2Dto(stock));
        }
        dto.setStocks(stocks);

        return dto;
    }

    public InventoryItem fromDto2Entity(InventoryItemCreateDto dto, Falla falla) {
        InventoryItem item = new InventoryItem();
        item.setDescription(dto.getDescription());
        item.setName(dto.getName());
        item.setItemCategory(ItemCategory.fromString(dto.getCategory()));
        item.setFalla(falla);
        item.setEnabled(true);
        return item;
    }
}
