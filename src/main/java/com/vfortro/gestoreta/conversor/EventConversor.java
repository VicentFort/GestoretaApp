package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.assists.AssistDTO;
import com.vfortro.gestoreta.dto.events.EventCreateDTO;
import com.vfortro.gestoreta.dto.events.EventInfoDTO;
import com.vfortro.gestoreta.dto.events.EventInfoUserDTO;
import com.vfortro.gestoreta.dto.food.info.FoodNeedInfoDTO;
import com.vfortro.gestoreta.dto.attendants.AttendantEventInfoDTO;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.model.enums.FoodNeedType;
import com.vfortro.gestoreta.repository.EventTagRepository;
import com.vfortro.gestoreta.repository.FallaRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public EventCreateDTO fromEntity2Dto(Event event) {
        EventCreateDTO dto = new EventCreateDTO();
        dto.setId(event.getId());
        dto.setPublicField(event.getPublicField());
        dto.setPrice(event.getPrice());
        dto.setDescription(event.getDescription());
        dto.setMaxPeople(event.getMaxPeople());
        dto.setDate(event.getDate().toLocalDate());
        dto.setFallaId(event.getFalla().getId());
        dto.setTagId(event.getEventTag().getId());
        dto.setTitle(event.getTitle());
        dto.setStartHour(event.getStartHour());
        dto.setEndHour(event.getEndHour());
        dto.setCreatedBy(event.getCreatedBy());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setEndDate(event.getEndDate().toLocalDate());
        dto.setActive(event.getActive());
        if(event.getAttendants()!= null && event.getAttendants().isEmpty()) {
            List<Long> atts = new ArrayList<>();
            for(Attendant att : event.getAttendants()) {
                atts.add(att.getUser().getId());
            }
            dto.setAttendants(atts);
        }

        return dto;
    }

    public EventInfoDTO fromEntity2InfoDto(Event event) {
        EventInfoDTO dto = new EventInfoDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setPublicField(event.getPublicField());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate().toLocalDate());
        dto.setPrice(event.getPrice());
        dto.setActive(event.getActive());
        dto.setTagName(event.getEventTag().getName());
        dto.setTagId(event.getEventTag().getId());
        dto.setStartHour(event.getStartHour());
        dto.setEndHour(event.getEndHour());
        dto.setCreatedBy(event.getCreatedBy());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setEndDate(event.getEndDate().toLocalDate());
        dto.setActive(event.getActive());
        dto.setCheckNeeds(event.getCheckNeeds());
        List<FoodNeedInfoDTO> needs = new ArrayList<>();
        List<AssistDTO> assists = new ArrayList<>();
        for(Assist assist : event.getAssists()) {
            assists.add(assistConversor.formEntity2Dto(assist));
            User user = assist.getUser();
            if(!user.getNeeds().isEmpty()) {
                for(FoodNeedType need : user.getNeeds()) {
                    FoodNeedInfoDTO aux = new FoodNeedInfoDTO();
                    aux.setFoodNeedType(need);
                    aux.setUserName(user.getName());
                    aux.setUserSurname(user.getSurname());
                    aux.setEventTitle(event.getTitle());
                    needs.add(aux);
                }
            }
        }
        dto.setAssists(assists);
        dto.setFoodNeeds(needs);
        List<AttendantEventInfoDTO> attendants = new ArrayList<>();
        for(Attendant att : event.getAttendants()) {
            AttendantEventInfoDTO attendantDto = new AttendantEventInfoDTO();
            attendantDto.setId(att.getUser().getId());
            attendantDto.setName(att.getUser().getName());
            attendantDto.setSurname(att.getUser().getSurname());
            attendants.add(attendantDto);
        }
        dto.setAttendants(attendants);
        return dto;
    }

    public Event fromDto2Entity(EventCreateDTO dto) {
        Event event = new Event();
        event.setId(dto.getId());
        event.setPublicField(dto.getPublicField());
        event.setActive(dto.getActive());
        event.setPrice(dto.getPrice());
        event.setDescription(dto.getDescription());
        event.setMaxPeople(dto.getMaxPeople());
        event.setDate(LocalDateTime.of(dto.getDate(),dto.getStartHour()));
        event.setTitle(dto.getTitle());
        event.setStartHour(dto.getStartHour());
        event.setEndHour(dto.getEndHour());
        event.setCreatedBy(dto.getCreatedBy());
        event.setCreatedAt(dto.getCreatedAt());
        event.setEndDate(LocalDateTime.of(dto.getEndDate(),dto.getEndHour()));
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

    public EventInfoUserDTO fromEntity2InfoUserDto(Event event) {
        EventInfoUserDTO dto = new EventInfoUserDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate().toLocalDate());
        dto.setPrice(event.getPrice());
        dto.setTagName(event.getEventTag().getName());
        dto.setStartHour(event.getStartHour());
        dto.setEndHour(event.getEndHour());
        dto.setCreatedBy(event.getCreatedBy());
        dto.setEndDate(event.getEndDate().toLocalDate());
        dto.setActive(event.getActive());
        return dto;
    }
}
