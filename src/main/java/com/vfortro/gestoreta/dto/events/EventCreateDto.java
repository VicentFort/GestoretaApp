package com.vfortro.gestoreta.dto.events;

import com.vfortro.gestoreta.dto.assists.AttendantCreateDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
    private @NotNull LocalDate date;
    private @NotNull Long fallaId;
    private @NotNull Long tagId;
    private LocalTime startHour;
    private LocalTime endHour;
    private List<Long> attendants;
    private @NotNull String createdBy;
    private LocalDateTime createdAt;
    private LocalDate endDate;
    private Boolean open;
}
