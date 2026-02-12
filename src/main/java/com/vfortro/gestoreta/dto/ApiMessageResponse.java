package com.vfortro.gestoreta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Respuesta genérica de esta API.", $comment = "Respuesta genérica de esta API.")
public class ApiMessageResponse {
    private String message;
    private Boolean success;
}
