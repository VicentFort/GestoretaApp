package com.vfortro.gestoreta.dto.charges;

import com.vfortro.gestoreta.model.enums.AccessType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ChargeUpdateDTO {
    @NotNull
    private AccessType accessType;
    @NotNull
    private Long userId;
}
