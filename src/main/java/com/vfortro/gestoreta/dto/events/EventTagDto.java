package com.vfortro.gestoreta.dto.events;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventTagDto {
    private Long id;
    private @NotNull Long fallaId;
}
