package com.vfortro.gestoreta.dto.users;

import com.vfortro.gestoreta.dto.assists.AttPrefInfoDto;
import com.vfortro.gestoreta.dto.food.FoodNeedCreateDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class UserInfoFallaDto {
    private Long id;
    private String name;
    private String surname;
    private LocalDate birthday;
    private Boolean showBday;
    private Boolean adminAccess;
    private LocalDate joinDate;
    private List<AttPrefInfoDto> prefs;
    private List<FoodNeedCreateDto> foodNeeds;
}
