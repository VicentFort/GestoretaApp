package com.vfortro.gestoreta.dto.events;

import com.vfortro.gestoreta.dto.assists.AssistDto;
import com.vfortro.gestoreta.dto.assists.AttendantCreateDto;
import com.vfortro.gestoreta.dto.food.FoodNeedResultDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventInfoDto {
    private @NotNull Long id;
    private String title;
    private String description;
    private Boolean done;
    private Boolean publicField;
    private Float price;
    private LocalDate date;
    private String tagName;
    private Long tagId;
    private List<FoodNeedResultDto> foodNeeds;
    private List<AssistDto> assists;
    private List<Long> attendantIds;
    private List<String> attendantNames;
    private LocalTime startHour;
    private LocalTime endHour;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDate endDate;
    private Boolean open;
    private Boolean checkNeeds;

}
