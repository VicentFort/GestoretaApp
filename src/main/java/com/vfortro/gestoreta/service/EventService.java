package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.AssistConversor;
import com.vfortro.gestoreta.conversor.EventConversor;
import com.vfortro.gestoreta.dto.*;
import com.vfortro.gestoreta.model.Assist;
import com.vfortro.gestoreta.model.Event;
import com.vfortro.gestoreta.model.FoodNeed;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.repository.EventRepository;
import com.vfortro.gestoreta.repository.EventTagRepository;
import com.vfortro.gestoreta.specification.EventSpecifications;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void createEvent(@Valid EventCreateDto event) throws EntityNotFoundException {
        if(Objects.isNull(fallaService.readFalla(event.getFallaId()))) {
            throw new EntityNotFoundException("La falla a la que se está asociando el evento: " + event.getTitle() + " no existe");
        }
        if(Objects.isNull(tagService.readEventTag(event.getTagId()))) {
            throw new EntityNotFoundException("La etiqueta a la que se está asociando el evento: " + event.getTitle() + " no existe");
        }
        eventRepository.save(eventConversor.fromDto2Entity(event));
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
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    @Transactional
    public EventCreateDto updateEvent(EventUpdateDto newEvent, Long eventId) {
        Event updatedEvent = eventRepository.findById(eventId).map(
                event -> {
                    if(newEvent.getDone() != null) event.setDone(newEvent.getDone());
                    if(newEvent.getPublicField() != null) event.setPublicField(newEvent.getPublicField());
                    if(newEvent.getTitle() != null) event.setTitle(newEvent.getTitle());
                    if(newEvent.getDescription() != null) event.setDescription(newEvent.getDescription());
                    if(newEvent.getDate() != null) event.setDate(newEvent.getDate());
                    if(newEvent.getTagId() != null && eventTagRepository.existsById(newEvent.getTagId())) {
                        event.setEventTag(eventTagRepository.findTagById(newEvent.getTagId()));
                    }
                    if(newEvent.getUpdatePrice() != null && newEvent.getUpdatePrice()) event.setPrice(newEvent.getPrice());
                    if(newEvent.getUpdateMaxPeople() != null && newEvent.getUpdateMaxPeople()) event.setMaxPeople(newEvent.getMaxPeople());
                    return eventRepository.saveAndFlush(event);
                }
        ).orElse(null);
        if(updatedEvent == null) return null;
        return eventConversor.fromEntity2Dto(updatedEvent);

    }

    @Transactional(readOnly = true)
    public List<AssistResultDto> getAssists(@Valid Long eventId) {
        Event event = eventRepository.findEventById(eventId);
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
    public List<FoodNeedResultDto> getFoodNeeds (Long eventId) {
        Event event = eventRepository.findEventById(eventId);
        List<Assist> eventAssists = event.getAssists().stream().toList();
        if(eventAssists.isEmpty()) throw new NullPointerException("El evento con id: " + eventId + " no tiene asistencias.");
        List<FoodNeedResultDto> needsDto = new ArrayList<>();
        for(Assist assist : eventAssists) {
            User user = assist.getUser();
            if(!user.getFoodNeeds().isEmpty()) {
                for(FoodNeed need : user.getFoodNeeds()) {
                    FoodNeedResultDto aux = new FoodNeedResultDto();
                    aux.setFoodNeedDesc(need.getDescription());
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
    public int getPeopleCount(Long eventId) {
        Event event = eventRepository.findEventById(eventId);
        return event.getAssists().size();
    }
}
