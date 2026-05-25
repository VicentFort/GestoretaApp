package com.vfortro.gestoreta.dto.events;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Data
public class EventCreateDTO {
    private Long id;
    private @NotNull String title;
    private @NotNull Boolean publicField;
    private Double price;
    private @NotNull String description;
    private Integer maxPeople;
    private @NotNull LocalDate date;
    private @NotNull Long fallaId;
    private @NotNull Long tagId;
    private LocalTime startHour;
    private LocalTime endHour;
    private List<Long> attendants;
    private @NotNull String createdBy;
    private LocalDateTime createdAt;
    private LocalDate endDate;
    private Boolean active;
    private Boolean checkNeeds;
}
