package com.vfortro.gestoreta.dto.events;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
@Data
public class EventCreateDto {
    private Long id;
    private @NotNull String title;
    private @NotNull Boolean publicField;
    private @NotNull Boolean done;
    private Float price;
    private @NotNull String description;
    private Integer maxPeople;
    private @NotNull Instant date;
    private @NotNull Long fallaId;
    private @NotNull Long tagId;


}
