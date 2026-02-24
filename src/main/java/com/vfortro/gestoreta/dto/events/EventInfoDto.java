package com.vfortro.gestoreta.dto.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EventInfoDto {
    private String title;
    private String description;
    private Boolean done;
    private Float price;
    private Instant date;
    private String tagName;

}
