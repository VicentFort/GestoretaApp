package com.vfortro.gestoreta.dto.inventory.stocks;

import com.vfortro.gestoreta.dto.inventory.loans.LoanInfoDto;
import com.vfortro.gestoreta.model.enums.LoanState;
import com.vfortro.gestoreta.model.enums.MovementType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class InventoryMovementInfoDto {
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

    private LoanInfoDto loan;

}
