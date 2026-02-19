package com.vfortro.gestoreta.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(example="vicente@email.com")
        String email,
        @Schema(example="1234")
        String password
) {
}
