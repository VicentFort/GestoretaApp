package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.assists.AttendantPrefInfoDto;
import com.vfortro.gestoreta.dto.events.EventInfoDto;
import com.vfortro.gestoreta.dto.events.EventInfoUserDto;
import com.vfortro.gestoreta.dto.events.EventTagInfoDto;
import com.vfortro.gestoreta.dto.fallas.FallaAdminInfo;
import com.vfortro.gestoreta.dto.fallas.FallaCreateDto;
import com.vfortro.gestoreta.dto.fallas.FallaUserInfoDto;
import com.vfortro.gestoreta.dto.requests.RequestInfoDto;
import com.vfortro.gestoreta.dto.users.UserInfoDto;
import com.vfortro.gestoreta.dto.users.UserInfoFallaDto;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.repository.EventRepository;
import com.vfortro.gestoreta.service.FallaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FallaConversor {
    private final EventConversor eventConversor;
    private final UserConversor userConversor;
    private final EventTagConversor eventTagConversor;
    private final RequestConversor requestConversor;
    @Autowired
    private EventRepository eventRepository;


    public FallaConversor(EventConversor eventConversor, UserConversor userConversor, EventTagConversor eventTagConversor, RequestConversor requestConversor) {
        this.eventConversor = eventConversor;
        this.userConversor = userConversor;
        this.eventTagConversor = eventTagConversor;
        this.requestConversor = requestConversor;
    }


    public Falla fromDto2Entity(FallaCreateDto dto) {
        Falla falla = new Falla();
        falla.setId(dto.getFallaId());
        falla.setName(dto.getName());
        falla.setCreationDate(dto.getCreationDate());
        falla.setShieldUrl(dto.getShieldUrl());
        return falla;
    }
    public FallaCreateDto fromEntity2DTO(Falla falla) {
        FallaCreateDto dto = new FallaCreateDto();
        dto.setFallaId(falla.getId());
        dto.setName(falla.getName());
        dto.setCreationDate(falla.getCreationDate());
        dto.setShieldUrl(falla.getShieldUrl());
        return dto;
    }



    public FallaAdminInfo fromEntity2AdminInfo(Falla falla) {
        List<EventTagInfoDto> tags = new ArrayList<>();
        List<UserInfoFallaDto> users = new ArrayList<>();
        List<EventInfoDto> events = new ArrayList<>();
        List<RequestInfoDto> requests = new ArrayList<>();
        List<AttendantPrefInfoDto> prefs = new ArrayList<>();
        FallaAdminInfo dto = new FallaAdminInfo();
        dto.setName(falla.getName());
        dto.setFallaId(falla.getId());

        for(Event event: falla.getEvents()) {
            events.add(eventConversor.fromEntity2InfoDto(event));
        }
        dto.setEvents(events);

        for(User user : falla.getUsers()) {
            users.add(userConversor.fromEntity2InfoFallaDto(user));
        }
        dto.setUsers(users);

        for(EventTag tag : falla.getEventTags()) {
            tags.add(eventTagConversor.fromEntity2Dto(tag));
        }
        dto.setTags(tags);

        for(Request req : falla.getRequests()) {
            requests.add(requestConversor.fromEntity2InfoDto(req));
        }
        dto.setRequests(requests);

        return dto;

    }
}
