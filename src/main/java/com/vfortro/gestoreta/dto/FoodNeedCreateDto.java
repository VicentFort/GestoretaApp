package com.vfortro.gestoreta.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FoodNeedCreateDto {
    private Long foodNeedId;
    private @NotNull String description;
    private @NotNull Long userId;
}
