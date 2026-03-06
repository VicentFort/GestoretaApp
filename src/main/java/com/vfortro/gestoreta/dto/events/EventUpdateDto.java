package com.vfortro.gestoreta.dto.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Dto enviado para actualizar un evento.")
public class EventUpdateDto {
    @Schema(example = "Event guay.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String title;
    @Schema(example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Boolean publicField;
    @Schema(example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Boolean done;
    @Schema(example = "6.0", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Float price;
    @Schema(example = "Event súper guay de la meua falla.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String description;
    @Schema(example = "75", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Integer maxPeople;
    @Schema(example = "2026-03-19T12:00:00.000Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private LocalDate date;
    @Schema(example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Long tagId;
    private LocalTime startHour;
    private LocalTime endHour;
}
