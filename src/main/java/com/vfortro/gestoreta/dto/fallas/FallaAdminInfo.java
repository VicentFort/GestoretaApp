package com.vfortro.gestoreta.dto.fallas;

import com.vfortro.gestoreta.dto.assists.AttendantCreateDto;
import com.vfortro.gestoreta.dto.assists.AttendantPrefInfoDto;
import com.vfortro.gestoreta.dto.events.EventInfoDto;
import com.vfortro.gestoreta.dto.events.EventTagInfoDto;
import com.vfortro.gestoreta.dto.requests.RequestInfoDto;
import com.vfortro.gestoreta.dto.users.UserInfoDto;
import com.vfortro.gestoreta.dto.users.UserInfoFallaDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FallaAdminInfo {
    private Long fallaId;
    private String name;
    private List<EventTagInfoDto> tags;
    private List<UserInfoFallaDto> users;
    private List<EventInfoDto> events;
    private List<RequestInfoDto> requests;
    private List<AttendantPrefInfoDto> attendants;

}
