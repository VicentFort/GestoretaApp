package com.vfortro.gestoreta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AssistDto {
    private Long assistId;
    private @NotNull Long userId;
    private @NotNull Long eventId;
    private Boolean paid;
}
