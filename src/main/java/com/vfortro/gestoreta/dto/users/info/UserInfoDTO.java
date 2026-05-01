package com.vfortro.gestoreta.dto.users.info;

import com.vfortro.gestoreta.dto.attendants.AttPrefInfoDTO;
import com.vfortro.gestoreta.dto.events.EventInfoUserDTO;
import com.vfortro.gestoreta.dto.fallas.info.FallaUserInfoDTO;
import com.vfortro.gestoreta.dto.food.FoodNeedCreateDTO;
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
public class UserInfoDTO {
    private Long id;
    private String name;
    private String surname;
    private String nickname;
    private LocalDateTime creationDate;
    private LocalDate birthday;
    private Boolean showBday;
    private Boolean adminAccess;
    private FallaUserInfoDTO fallaInfo;
    private LocalDate joinDate;
    private List<EventInfoUserDTO> events;
    private List<EventInfoUserDTO> attEvents;
    private List<FoodNeedCreateDTO> foodNeeds;
    private List<AttPrefInfoDTO> eventTagPrefs;


}
