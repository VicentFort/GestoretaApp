package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.assists.AssistDto;
import com.vfortro.gestoreta.dto.assists.AttendantCreateDto;
import com.vfortro.gestoreta.dto.events.EventCreateDto;
import com.vfortro.gestoreta.dto.events.EventInfoDto;
import com.vfortro.gestoreta.dto.events.EventInfoUserDto;
import com.vfortro.gestoreta.dto.food.FoodNeedResultDto;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.repository.EventTagRepository;
import com.vfortro.gestoreta.repository.FallaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventConversor {

    @Autowired
    private FallaRepository fallaRepository;
    @Autowired
    private EventTagRepository tagRepository;
    @Autowired
    private AssistConversor assistConversor;

    public EventCreateDto fromEntity2Dto(Event event) {
        EventCreateDto dto = new EventCreateDto();
        dto.setId(event.getId());
        dto.setPublicField(event.getPublicField());
        dto.setDone(event.getDone());
        dto.setPrice(event.getPrice());
        dto.setDescription(event.getDescription());
        dto.setMaxPeople(event.getMaxPeople());
        dto.setDate(event.getDate().toLocalDate());
        dto.setFallaId(event.getFalla().getId());
        dto.setTagId(event.getEventTag().getId());
        dto.setTitle(event.getTitle());
        dto.setStartHour(event.getStartHour());
        dto.setEndHour(event.getEndHour());
        dto.setCreatedBy(event.getCreatdBy());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setEndDate(event.getEndDate().toLocalDate());
        dto.setOpen(event.getOpen());
        if(event.getAttendants()!= null && event.getAttendants().isEmpty()) {
            List<Long> atts = new ArrayList<>();
            for(Attendant att : event.getAttendants()) {
                atts.add(att.getUsers().getId());
            }
            dto.setAttendants(atts);
        }

        return dto;
    }

    public EventInfoDto fromEntity2InfoDto(Event event) {
        EventInfoDto dto = new EventInfoDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setPublicField(event.getPublicField());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate().toLocalDate());
        dto.setPrice(event.getPrice());
        dto.setDone(event.getDone());
        dto.setTagName(event.getEventTag().getName());
        dto.setTagId(event.getEventTag().getId());
        dto.setStartHour(event.getStartHour());
        dto.setEndHour(event.getEndHour());
        dto.setCreatedBy(event.getCreatdBy());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setEndDate(event.getEndDate().toLocalDate());
        dto.setOpen(event.getOpen());
        dto.setCheckNeeds(event.getCheckNeeds());
        List<FoodNeedResultDto> needs = new ArrayList<>();
        List<String> uNames = new ArrayList<>();
        List<Long> uIds = new ArrayList<>();
        List<AssistDto> assists = new ArrayList<>();
        for(Assist assist : event.getAssists()) {
            assists.add(assistConversor.formEntity2Dto(assist));
            User user = assist.getUser();
            if(!user.getFoodNeeds().isEmpty()) {
                for(FoodNeed need : user.getFoodNeeds()) {
                    FoodNeedResultDto aux = new FoodNeedResultDto();
                    aux.setFoodNeedDesc(need.getDescription().getValue());
                    aux.setUserName(user.getName());
                    aux.setUserSurname(user.getSurname());
                    aux.setEventTitle(event.getTitle());
                    needs.add(aux);
                }
            }
        }
        dto.setAssists(assists);
        dto.setFoodNeeds(needs);
        for(Attendant att : event.getAttendants()) {
            uNames.add(att.getUsers().getName());
            uIds.add(att.getUsers().getId());
        }
        dto.setAttendantNames(uNames);
        dto.setAttendantIds(uIds);
        return dto;
    }

    public Event fromDto2Entity(EventCreateDto dto) {
        Event event = new Event();
        event.setId(dto.getId());
        event.setPublicField(dto.getPublicField());
        event.setDone(dto.getDone());
        event.setPrice(dto.getPrice());
        event.setPrice(dto.getPrice());
        event.setDescription(dto.getDescription());
        event.setMaxPeople(dto.getMaxPeople());
        event.setDate(LocalDateTime.of(dto.getDate(),dto.getStartHour()));
        event.setTitle(dto.getTitle());
        event.setStartHour(dto.getStartHour());
        event.setEndHour(dto.getEndHour());
        event.setCreatdBy(dto.getCreatedBy());
        event.setCreatedAt(dto.getCreatedAt());
        event.setEndDate(LocalDateTime.of(dto.getEndDate(),dto.getEndHour()));
        event.setOpen(dto.getOpen());
        event.setCheckNeeds(dto.getCheckNeeds());
        if(dto.getTagId() == null) throw new NullPointerException("El evento debe tener una id de etiqueta de evento");
        if(!tagRepository.existsById(dto.getTagId())) throw new EntityNotFoundException("La etiqueta asignada al evento no existe en la base de datos.");
        EventTag tag = tagRepository.findById(dto.getTagId()).orElse(null);

        if(dto.getFallaId() == null) throw new NullPointerException("El evento debe tener una id de falla");
        if(!fallaRepository.existsById(dto.getFallaId())) throw new EntityNotFoundException("La falla asignada al evento no existe");
        Falla falla = fallaRepository.findById(dto.getFallaId()).orElse(null);

        event.setEventTag(tag);
        event.setFalla(falla);

        return event;
    }

    public EventInfoUserDto fromEntity2InfoUserDto(Event event) {
        EventInfoUserDto dto = new EventInfoUserDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate().toLocalDate());
        dto.setPrice(event.getPrice());
        dto.setDone(event.getDone());
        dto.setTagName(event.getEventTag().getName());
        dto.setStartHour(event.getStartHour());
        dto.setEndHour(event.getEndHour());
        dto.setCreatedBy(event.getCreatdBy());
        dto.setEndDate(event.getEndDate().toLocalDate());
        dto.setOpen(event.getOpen());
        return dto;
    }
}
