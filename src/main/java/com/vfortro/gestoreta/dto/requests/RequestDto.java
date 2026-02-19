package com.vfortro.gestoreta.dto.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Dto enviado para crear una solicitud.")
@NoArgsConstructor
@Getter
@Setter
public class RequestDto {
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Long requestId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = false)
    private @NotNull Long idUser;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = false)
    private @NotNull Long idFalla;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = false)
    private @NotNull String message;
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Boolean aproved;
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String reply;
}
