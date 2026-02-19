package com.vfortro.gestoreta.dto.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AttendantPreferenceInfoDto {
    private Long id;
    private String eventTagName;
    private String userName;
    private String userSurname;
}
