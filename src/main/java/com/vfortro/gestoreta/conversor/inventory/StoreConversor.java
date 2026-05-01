package com.vfortro.gestoreta.conversor.inventory;

import com.vfortro.gestoreta.dto.inventory.stocks.StockInfoDTO;
import com.vfortro.gestoreta.dto.inventory.stores.StoreCreateDTO;
import com.vfortro.gestoreta.dto.inventory.stores.StoreInfoDTO;
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

    public StoreInfoDTO fromEntity2Dto(Store store){
        StoreInfoDTO dto = new StoreInfoDTO();
        dto.setId(store.getStoreId());
        dto.setName(store.getName());
        dto.setLocation(store.getLocation());
        dto.setEnabled(store.getEnabled());
        List<StockInfoDTO> stocks = new ArrayList<>();
        for(Stock stock : store.getStocks()){
            stocks.add(stockConversor.fromEntity2Dto(stock));
        }
        dto.setStocks(stocks);
        return dto;
    }

    public Store fromDto2Entity(StoreCreateDTO dto, Falla falla){
        Store store = new Store();
        store.setName(dto.getName());
        store.setLocation(dto.getLocation());
        store.setFalla(falla);
        store.setEnabled(true);
        return store;
    }
}

