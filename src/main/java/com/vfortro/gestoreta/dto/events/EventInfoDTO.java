package com.vfortro.gestoreta.dto.events;

import com.vfortro.gestoreta.dto.assists.AssistDTO;
import com.vfortro.gestoreta.dto.food.info.FoodNeedInfoDTO;
import com.vfortro.gestoreta.dto.attendants.AttendantEventInfoDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventInfoDTO {
    private @NotNull Long id;
    private String title;
    private String description;
    private Boolean active;
    private Boolean publicField;
    private Double price;
    private LocalDate date;
    private String tagName;
    private Long tagId;
    private List<FoodNeedInfoDTO> foodNeeds;
    private List<AssistDTO> assists;
    private List<AttendantEventInfoDTO> attendants;
    private LocalTime startHour;
    private LocalTime endHour;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDate endDate;
    private Boolean checkNeeds;
    private Double totalRevenue;
    private byte[] image;

}
