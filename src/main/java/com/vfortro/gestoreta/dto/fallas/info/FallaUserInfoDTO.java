package com.vfortro.gestoreta.dto.fallas.info;

import com.vfortro.gestoreta.dto.events.EventInfoUserDTO;
import com.vfortro.gestoreta.dto.events.EventTagAdminInfoDTO;
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
public class FallaUserInfoDTO {
    private Long fallaId;
    private String name;
    private String shield;
    private LocalDate creationDate;
    private List<EventInfoUserDTO> events;
    private List<EventTagAdminInfoDTO> tags;

}
