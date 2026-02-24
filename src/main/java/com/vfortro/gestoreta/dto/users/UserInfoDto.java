package com.vfortro.gestoreta.dto.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    private String name;
    private String surname;
    private LocalDate birthday;
    private Boolean showBday;
    private Boolean adminAccess;
    private List<Long> assistEventIds;
    private List<String> assistEventTitles;

}
