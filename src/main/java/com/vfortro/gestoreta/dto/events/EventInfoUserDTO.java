package com.vfortro.gestoreta.dto.events;

import com.vfortro.gestoreta.dto.assists.AssistDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class EventInfoUserDTO {
    private @NotNull Long id;
    private String title;
    private String description;
    private Double price;
    private LocalDate date;
    private LocalDate endDate;
    private String tagName;
    private LocalTime startHour;
    private LocalTime endHour;
    private String createdBy;
    private Boolean active;
    private AssistDTO assist;
    private byte[] image;
}
