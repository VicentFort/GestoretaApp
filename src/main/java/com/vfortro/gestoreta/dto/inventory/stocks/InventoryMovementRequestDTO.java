package com.vfortro.gestoreta.dto.inventory.stocks;

import com.vfortro.gestoreta.model.enums.MovementType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class InventoryMovementRequestDTO {
    private @NotNull Long itemId;
    private @NotNull Long storeId;
    private @NotNull Long amount;
    private @NotNull MovementType type;
    private @NotNull String message;

    //En caso de que sea un préstamo.
    private Long contactId;
    private LocalDateTime adquisitionDate;
    private LocalDateTime idealReturnDate;
}
