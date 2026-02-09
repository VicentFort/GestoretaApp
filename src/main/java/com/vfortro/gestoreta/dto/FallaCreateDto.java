package com.vfortro.gestoreta.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FallaCreateDto {
    private Long fallaId;
    private @NotNull String name;
    private @NotNull LocalDate creationDate;
    private String shieldUrl;
}
