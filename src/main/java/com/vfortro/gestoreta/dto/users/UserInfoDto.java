package com.vfortro.gestoreta.dto.users;

import com.vfortro.gestoreta.dto.assists.AttPrefInfoDto;
import com.vfortro.gestoreta.dto.events.EventInfoUserDto;
import com.vfortro.gestoreta.dto.fallas.FallaUserInfoDto;
import com.vfortro.gestoreta.dto.food.FoodNeedCreateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    private Long id;
    private String name;
    private String surname;
    private LocalDateTime creationDate;
    private LocalDate birthday;
    private Boolean showBday;
    private Boolean adminAccess;
    private FallaUserInfoDto fallaInfo;
    private LocalDate joinDate;
    private List<EventInfoUserDto> events;
    private List<EventInfoUserDto> attEvents;
    private List<FoodNeedCreateDto> foodNeeds;
    private List<AttPrefInfoDto> eventTagPrefs;


}
