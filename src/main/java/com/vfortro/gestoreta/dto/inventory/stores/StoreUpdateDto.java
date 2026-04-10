package com.vfortro.gestoreta.dto.inventory.stores;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreUpdateDto {
    @NotNull
    private Long storeId;
    private String name;
    private String location;
    private Boolean enabled;
}
