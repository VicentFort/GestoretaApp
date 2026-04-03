package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.inventory.stocks.StockInfoDto;
import com.vfortro.gestoreta.model.inventory.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockConversor {

    @Autowired
    private InventoryItemConversor inventoryItemConversor;

    public StockInfoDto fromEntity2Dto(Stock stock) {
        StockInfoDto dto = new StockInfoDto();
        dto.setId(stock.getStockId());
        dto.setAmount(stock.getAmount());
        dto.setItem(inventoryItemConversor.fromEntity2Dto(stock.getInventoryItem()));
        return dto;
    }
}
