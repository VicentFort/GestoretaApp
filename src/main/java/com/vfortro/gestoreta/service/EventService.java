package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.AssistConversor;
import com.vfortro.gestoreta.conversor.EventConversor;
import com.vfortro.gestoreta.dto.assists.AssistResultDto;
import com.vfortro.gestoreta.dto.events.EventCreateDto;
import com.vfortro.gestoreta.dto.events.EventFilter;
import com.vfortro.gestoreta.dto.events.EventUpdateDto;
import com.vfortro.gestoreta.dto.food.FoodNeedResultDto;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.repository.*;
import com.vfortro.gestoreta.specification.EventSpecifications;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private FallaService fallaService;
    @Autowired
    private EventTagService tagService;
    @Autowired
    private EventConversor eventConversor;
    @Autowired
    private AssistConversor assistConversor;
    @Autowired
    private EventTagRepository eventTagRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private FallaRepository fallaRepository;
    @Autowired
    private AttendantRepository attendantRepository;


    @Transactional(readOnly = true)
    public EventCreateDto readEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if(event == null) return null;
        return eventConversor.fromEntity2Dto(event);
    }

    @Transactional(readOnly = true)
    public EventCreateDto readEvent(@NotNull String title) {
        Event event = eventRepository.findEventByTitle(title);
        if(event == null) return null;
        return eventConversor.fromEntity2Dto(event);
    }

    @Transactional
    public EventCreateDto createEvent(@Valid EventCreateDto event, String email) throws EntityNotFoundException, AccessDeniedException {
        if(Objects.isNull(fallaService.readFalla(event.getFallaId()))) {
            throw new EntityNotFoundException("La falla a la que se está asociando el evento: " + event.getTitle() + " no existe");
        }
        if(Objects.isNull(tagService.readEventTag(event.getTagId()))) {
            throw new EntityNotFoundException("La etiqueta a la que se está asociando el evento: " + event.getTitle() + " no existe");
        }
        if(!Objects.equals(event.getFallaId(), userService.readUser(email).getFallaId())) throw new AccessDeniedException("Sin permiso para esta falla.");
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        Event toSave = eventConversor.fromDto2Entity(event);
        Event saved = eventRepository.saveAndFlush(toSave);

        if(event.getAttendants() != null && !event.getAttendants().isEmpty()) {
            for (Long userId : event.getAttendants()) {
                Attendant attendant = new Attendant();
                attendant.setEvent(saved);
                attendant.setFalla(saved.getFalla());
                User user = userService.readUserAsEntity(userId);
                attendant.setUsers(user);

                attendantRepository.saveAndFlush(attendant);
            }

        }
        return eventConversor.fromEntity2Dto(saved);
    }
    @Transactional(readOnly = true)
    public List<EventCreateDto> findByFilters(Long fallaId, EventFilter filters) {
        List<Event> eventsFiltered = eventRepository.findAll(
                Specification.where(EventSpecifications.hasTitle(filters.title()))
                        .and(EventSpecifications.hasEventTag(filters.tagId()))
                        .and(EventSpecifications.isPublic(filters.isPublic()))
                        .and(EventSpecifications.isDone(filters.isDone()))
                        .and(EventSpecifications.hasPrice(filters.price()))
                        .and(EventSpecifications.isOnDate(filters.date()))
                        .and(EventSpecifications.isFromFalla(fallaId))
        );
        List<EventCreateDto> dtos = new ArrayList<>();
        for(Event event : eventsFiltered) {
            dtos.add(eventConversor.fromEntity2Dto(event));
        }
        return dtos;
    }

    @Transactional
    public void deleteEvent(Long eventId, String email) throws AccessDeniedException {
        Event event = eventRepository.findEventById(eventId);
        if(!Objects.equals(event.getFalla().getId(), userService.readUser(email).getFallaId())) throw new AccessDeniedException("Sin permiso para esta falla.");
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        eventRepository.delete(event);
    }

    @Transactional
    public EventCreateDto updateEvent(EventUpdateDto newEvent, Long eventId, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        Event updatedEvent = eventRepository.findEventById(eventId);
        if(!Objects.equals(updatedEvent.getFalla().getId(), userService.readUser(email).getFallaId())) {
           throw new AccessDeniedException("Sin permiso para esta falla.");
        }
        if(newEvent.getDone() != null) updatedEvent.setDone(newEvent.getDone());
        if(newEvent.getPublicField() != null) updatedEvent.setPublicField(newEvent.getPublicField());
        if(newEvent.getTitle() != null) updatedEvent.setTitle(newEvent.getTitle());
        if(newEvent.getDescription() != null) updatedEvent.setDescription(newEvent.getDescription());
        if(newEvent.getDate() != null) updatedEvent.setDate(LocalDateTime.of(newEvent.getDate(),newEvent.getStartHour()));
        if(newEvent.getTagId() != null && eventTagRepository.existsById(newEvent.getTagId())) {
            updatedEvent.setEventTag(eventTagRepository.findTagById(newEvent.getTagId()));
        }
        if(newEvent.getPrice() != null) updatedEvent.setPrice(newEvent.getPrice());
        if(newEvent.getMaxPeople() != null) updatedEvent.setMaxPeople(newEvent.getMaxPeople());
        if(newEvent.getStartHour() != null) updatedEvent.setStartHour(newEvent.getStartHour());
        if(newEvent.getEndHour() != null) updatedEvent.setEndHour(newEvent.getEndHour());
        if(newEvent.getEndDate() != null && newEvent.getEndHour() != null) updatedEvent.setEndDate(LocalDateTime.of(newEvent.getEndDate(),newEvent.getEndHour()));
        return eventConversor.fromEntity2Dto(eventRepository.saveAndFlush(updatedEvent));

    }

    @Transactional(readOnly = true)
    public List<AssistResultDto> getAssists(@Valid Long eventId, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        Event event = eventRepository.findEventById(eventId);
        if(!Objects.equals(event.getFalla().getId(), userService.readUser(email).getFallaId())) throw new AccessDeniedException("Sin permiso para esta falla.");
        List<AssistResultDto> dtoList = new ArrayList<>();
        for(Assist assist : event.getAssists()) {
            AssistResultDto assistDto = new AssistResultDto();
            assistDto.setEventTitle(event.getTitle());
            assistDto.setUserName(assist.getUser().getName());
            assistDto.setUserSurname(assist.getUser().getSurname());
            dtoList.add(assistDto);
        }
        return dtoList;
    }

    @Transactional(readOnly = true)
    public List<FoodNeedResultDto> getFoodNeeds (Long eventId, String email) throws AccessDeniedException {
        Event event = eventRepository.findEventById(eventId);
        if(!Objects.equals(event.getFalla().getId(), userService.readUser(email).getFallaId())) throw new AccessDeniedException("Sin permiso para esta falla.");
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        List<Assist> eventAssists = event.getAssists().stream().toList();
        if(eventAssists.isEmpty()) throw new NullPointerException("El evento con id: " + eventId + " no tiene asistencias.");
        List<FoodNeedResultDto> needsDto = new ArrayList<>();
        for(Assist assist : eventAssists) {
            User user = assist.getUser();
            if(!user.getFoodNeeds().isEmpty()) {
                for(FoodNeed need : user.getFoodNeeds()) {
                    FoodNeedResultDto aux = new FoodNeedResultDto();
                    aux.setFoodNeedDesc(need.getDescription().getValue());
                    aux.setUserName(user.getName());
                    aux.setUserSurname(user.getSurname());
                    aux.setEventTitle(event.getTitle());
                    needsDto.add(aux);
                }
            }
        }
        return needsDto;
    }

    @Transactional(readOnly = true)
    public int getPeopleCount(Long eventId,String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        Event event = eventRepository.findEventById(eventId);
        if(!Objects.equals(event.getFalla().getId(), userService.readUser(email).getFallaId())) throw new AccessDeniedException("Sin permiso a esta falla.");
        return event.getAssists().size();
    }


}
