package com.vfortro.gestoreta.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestDto {
    private Long requestId;
    private @NotNull Long idUser;
    private @NotNull Long idFalla;
    private @NotNull String message;
    private Boolean aproved;
    private String reply;
}
