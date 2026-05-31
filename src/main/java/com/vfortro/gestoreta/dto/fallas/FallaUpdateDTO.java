package com.vfortro.gestoreta.dto.fallas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Dto enviado para actualizar una falla.")
public class FallaUpdateDTO {
    @NotNull
    private Long id;
    private String description;
    private Boolean openRequests;

}
