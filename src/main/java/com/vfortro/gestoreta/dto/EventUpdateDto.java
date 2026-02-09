package com.vfortro.gestoreta.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventUpdateDto {
    private Long id;
    private String title;
    private Boolean publicField;
    private Boolean done;
    private Float price;
    private String description;
    private Integer maxPeople;
    private Instant date;
    private Long fallaId;
    private Long tagId;
    private @NotNull Boolean updatePrice;
    private @NotNull Boolean updateMaxPeople;
}
