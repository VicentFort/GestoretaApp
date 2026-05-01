package com.vfortro.gestoreta.conversor.inventory;

import com.vfortro.gestoreta.dto.inventory.stocks.StockInfoDTO;
import com.vfortro.gestoreta.model.inventory.Stock;
import org.springframework.stereotype.Service;

@Service
public class StockConversor {


    public StockInfoDTO fromEntity2Dto(Stock stock) {
        StockInfoDTO dto = new StockInfoDTO();
        dto.setStockId(stock.getStockId());
        dto.setAmount(stock.getAmount());
        dto.setItemId(stock.getInventoryItem().getItemId());
        dto.setItemName(stock.getInventoryItem().getName());
        dto.setStoreId(stock.getStore().getStoreId());
        dto.setStoreName(stock.getStore().getName());
        return dto;
    }


}
