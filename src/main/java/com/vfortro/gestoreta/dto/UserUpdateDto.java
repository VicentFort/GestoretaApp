package com.vfortro.gestoreta.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.Example;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import tools.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDate;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Dto enviado para actualizar un usuario.")
public class UserUpdateDto {
    @Schema(example = "Miguel", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String name;
    @Schema(example = "García Martínez", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String surname;
    @Schema(example = "2004-01-01", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private LocalDate birthday;
    @Schema(example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Boolean showBday;
    @Schema(example = "", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String urlPfp;
}


