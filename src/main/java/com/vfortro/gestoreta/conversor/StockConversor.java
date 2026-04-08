package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.inventory.stocks.StockInfoDto;
import com.vfortro.gestoreta.model.inventory.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockConversor {



    public StockInfoDto fromEntity2Dto(Stock stock) {
        StockInfoDto dto = new StockInfoDto();
        dto.setId(stock.getStockId());
        dto.setAmount(stock.getAmount());
        dto.setItemId(stock.getInventoryItem().getItemId());
        return dto;
    }


}
