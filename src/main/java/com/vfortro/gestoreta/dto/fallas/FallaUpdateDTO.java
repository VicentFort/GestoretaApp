package com.vfortro.gestoreta.dto.fallas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Dto enviado para actualizar una falla.")
public class FallaUpdateDTO {
    @Schema(example = "Falla súper guay.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String name;
    @Schema(example = "2000-01-01", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private LocalDate creationDate;

}
