package com.vfortro.gestoreta.dto.users.info;

import com.vfortro.gestoreta.dto.attendants.AttPrefInfoDTO;
import com.vfortro.gestoreta.dto.food.FoodNeedCreateDTO;
import com.vfortro.gestoreta.model.enums.AccessType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class UserInfoFallaDTO {
    private Long id;
    private String name;
    private String surname;
    private String nickname;
    private LocalDate birthday;
    private Boolean showBday;
    private AccessType accessType;
    private LocalDate joinDate;
    private List<AttPrefInfoDTO> prefs;
    private List<FoodNeedCreateDTO> foodNeeds;
}
