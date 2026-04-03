package com.vfortro.gestoreta.dto.inventory.stores;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class StoreCreateDto {
    private String name;
    private String location;
}
