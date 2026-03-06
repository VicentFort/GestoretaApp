package com.vfortro.gestoreta.dto.events;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class EventInfoUserDto {
    private @NotNull Long id;
    private String title;
    private String description;
    private Boolean done;
    private Float price;
    private LocalDate date;
    private String tagName;
    private LocalTime startHour;
    private LocalTime endHour;
    private String createdBy;
}
