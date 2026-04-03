package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.inventory.stocks.StockInfoDto;
import com.vfortro.gestoreta.dto.inventory.stores.StoreCreateDto;
import com.vfortro.gestoreta.dto.inventory.stores.StoreInfoDto;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.inventory.Stock;
import com.vfortro.gestoreta.model.inventory.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StoreConversor {
    @Autowired
    private StockConversor stockConversor;

    public StoreInfoDto fromEntity2Dto(Store store){
        StoreInfoDto dto = new StoreInfoDto();
        dto.setId(store.getStoreId());
        dto.setName(store.getName());
        dto.setLocation(store.getLocation());
        List<StockInfoDto> stocks = new ArrayList<>();
        for(Stock stock : store.getStocks()){
            stocks.add(stockConversor.fromEntity2Dto(stock));
        }
        dto.setStocks(stocks);
        return dto;
    }

    public Store fromDto2Entity(StoreCreateDto dto, Falla falla){
        Store store = new Store();
        store.setName(dto.getName());
        store.setLocation(dto.getLocation());
        store.setFalla(falla);
        return store;
    }
}

