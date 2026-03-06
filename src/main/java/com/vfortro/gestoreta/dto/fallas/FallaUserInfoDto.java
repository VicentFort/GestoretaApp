package com.vfortro.gestoreta.dto.fallas;

import com.vfortro.gestoreta.dto.events.EventInfoUserDto;
import com.vfortro.gestoreta.dto.events.EventTagInfoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FallaUserInfoDto {
    private Long fallaId;
    private String name;
    private LocalDate creationDate;
    private List<EventInfoUserDto> events;
    private List<EventTagInfoDto> tags;

}
