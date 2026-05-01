package com.vfortro.gestoreta.dto.fallas;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FallaCreateDTO {
    private Long fallaId;
    private @NotNull String name;
    private @NotNull LocalDate creationDate;
    private String shieldUrl;
}
