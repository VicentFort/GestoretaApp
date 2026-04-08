package com.vfortro.gestoreta.conversor;


import com.vfortro.gestoreta.dto.inventory.stocks.InventoryMovementInfoDto;
import com.vfortro.gestoreta.model.enums.MovementType;
import com.vfortro.gestoreta.model.inventory.InventoryMovement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryMovementConversor {

    @Autowired
    private LoanConversor loanConversor;

    public InventoryMovementInfoDto fromEntity2Dto(InventoryMovement movement) {
        InventoryMovementInfoDto dto = new InventoryMovementInfoDto();
        dto.setItemId(movement.getItem().getItemId());
        dto.setItemName(movement.getItem().getName());
        dto.setStoreId(movement.getStore().getStoreId());
        dto.setStoreName(movement.getStore().getName());
        dto.setAmount(movement.getAmount());
        dto.setDate(movement.getDate());
        dto.setMovementType(movement.getType());
        dto.setMessage(movement.getMessage());
        dto.setCreatedBy(movement.getCreatedBy());
        if(movement.getType().equals(MovementType.LOAN)) {
            dto.setLoan(loanConversor.fromEntity2Dto(movement.getLoan()));
        }
        return dto;
    }
}
