package com.vfortro.gestoreta.dto.inventory.stocks;

import com.vfortro.gestoreta.dto.inventory.loans.LoanInfoDTO;
import com.vfortro.gestoreta.model.enums.MovementType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class InventoryMovementInfoDTO {
    private Long id;
    private Long itemId;
    private String itemName;
    private Long storeId;
    private String storeName;
    private Long amount;
    private LocalDateTime date;
    private MovementType movementType;
    private String message;
    private String createdBy;

    private LoanInfoDTO loan;

}
