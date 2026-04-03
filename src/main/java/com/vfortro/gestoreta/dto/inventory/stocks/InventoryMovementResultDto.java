package com.vfortro.gestoreta.dto.inventory.stocks;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryMovementResultDto {
    private Long incomingAmount;
    private Long finalAmount;
    private Long itemId;
    private String itemName;
    private Long storeId;
    private String storeName;
    private String message;

    @Override
    public String toString() {
        return "S'han menejat: " + incomingAmount + " unitats del item: " + itemName + " al magatzem: " + storeName + ". Ara hi han: " + finalAmount + " unitats.";
    }
}
