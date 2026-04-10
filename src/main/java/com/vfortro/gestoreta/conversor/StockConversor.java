package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.inventory.stocks.StockInfoDto;
import com.vfortro.gestoreta.model.inventory.Stock;
import org.springframework.stereotype.Service;

@Service
public class StockConversor {


    public StockInfoDto fromEntity2Dto(Stock stock) {
        StockInfoDto dto = new StockInfoDto();
        dto.setStockId(stock.getStockId());
        dto.setAmount(stock.getAmount());
        dto.setItemId(stock.getInventoryItem().getItemId());
        dto.setItemName(stock.getInventoryItem().getName());
        dto.setStoreId(stock.getStore().getStoreId());
        dto.setStoreName(stock.getStore().getName());
        return dto;
    }


}
