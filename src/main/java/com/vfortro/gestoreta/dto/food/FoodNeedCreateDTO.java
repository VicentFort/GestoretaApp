package com.vfortro.gestoreta.dto.food;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FoodNeedCreateDTO {
    private Long foodNeedId;
    private @NotNull String description;
    private @NotNull Long userId;
}
