package com.vfortro.gestoreta.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(example="vicent@email.com")
        String email,
        @Schema(example="1234")
        String password
) {
}
