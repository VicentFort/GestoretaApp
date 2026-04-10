package com.vfortro.gestoreta.dto.inventory.stocks;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class StockInfoDto {
    private Long stockId;
    private Long amount;
    private Long itemId;
    private String itemName;
    private Long storeId;
    private String storeName;
}
