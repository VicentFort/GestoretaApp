package com.vfortro.gestoreta.dto.assists;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Dto para la creación de asistencias.")
public class AssistDto {
    private Long assistId;
    private @NotNull Long userId;
    private @NotNull Long eventId;
    private Boolean paid;
}
