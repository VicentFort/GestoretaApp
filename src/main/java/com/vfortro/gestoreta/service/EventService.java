package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.AssistConversor;
import com.vfortro.gestoreta.conversor.EventConversor;
import com.vfortro.gestoreta.conversor.EventTagConversor;
import com.vfortro.gestoreta.dto.assists.AssistDTO;
import com.vfortro.gestoreta.dto.events.EventCreateDTO;
import com.vfortro.gestoreta.dto.events.EventTagAdminInfoDTO;
import com.vfortro.gestoreta.dto.events.EventUpdateDTO;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EventService {


    //REPOSITORIES
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private AssistRepository assistRepository;
    @Autowired
    private EventTagRepository eventTagRepository;
    @Autowired
    private FallaRepository fallaRepository;
    @Autowired
    private AttendantRepository attendantRepository;
    @Autowired
    private UserRepository userRepository;

    //CONVERSORS
    @Autowired
    private EventConversor eventConversor;
    @Autowired
    private AssistConversor assistConversor;
    @Autowired
    private EventTagConversor eventTagConversor;


    //SERVICES
    @Autowired
    private UserService userService;


    @Transactional(readOnly = true)
    public EventCreateDTO readEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if(event == null) return null;
        return eventConversor.fromEntity2Dto(event);
    }


    @Transactional
    public EventCreateDTO createEvent(@Valid EventCreateDTO event, String email) throws EntityNotFoundException, AccessDeniedException {
        if(fallaRepository.existsById(event.getId())) {
            throw new EntityNotFoundException("La falla a la que se está asociando el evento: " + event.getTitle() + " no existe");
        }
        if(Objects.isNull(readEventTag(event.getTagId()))) {
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
                attendant.setUser(user);

                attendantRepository.saveAndFlush(attendant);
            }

        }
        return eventConversor.fromEntity2Dto(saved);
    }

    @Transactional
    public void deleteEvent(Long eventId, String email) throws AccessDeniedException {
        Event event = eventRepository.findEventById(eventId);
        if(!Objects.equals(event.getFalla().getId(), userService.readUser(email).getFallaId())) throw new AccessDeniedException("Sin permiso para esta falla.");
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        eventRepository.delete(event);
    }

    @Transactional
    public EventCreateDTO updateEvent(EventUpdateDTO newEvent, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        Event updatedEvent = eventRepository.findEventById(newEvent.getEventId());
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


        if(newEvent.getAttendantIds() != null) {
            updatedEvent.getAttendants().clear();
            for (Long userId : newEvent.getAttendantIds()) {
                // Buscamos el usuario
                User user = userRepository.findUserById(userId);
                if(attendantRepository.existsByUser_IdAndEvent_Id(userId, newEvent.getEventId())) {
                    break;
                }
                if (user != null) {
                    Attendant attendant = new Attendant();
                    attendant.setUser(user);
                    attendant.setEvent(updatedEvent);
                    attendant.setFalla(updatedEvent.getFalla());

                    // Añadimos a la colección del evento
                    updatedEvent.getAttendants().add(attendant);
                }
            }

        }

        return eventConversor.fromEntity2Dto(eventRepository.saveAndFlush(updatedEvent));

    }

    @Transactional(readOnly = true)
    public AssistDTO readAssist(String email, Long eventId) {
        Assist assist = assistRepository.findByUserEmailAndEventId(email, eventId);
        if(assist == null) return null;
        return assistConversor.formEntity2Dto(assist);
    }

    @Transactional
    public AssistDTO createAssist(String email, Long eventId) {
        AssistDTO dto = new AssistDTO();
        dto.setUserId(userService.readUser(email).getUserId());
        dto.setEventId(eventId);
        Assist saved = assistRepository.saveAndFlush(assistConversor.fromDto2Entity(dto));
        return assistConversor.formEntity2Dto(saved);
    }

    @Transactional
    public void deleteAssist(Long assistId) {
        assistRepository.deleteById(assistId);
    }

    @Transactional(readOnly = true)
    public EventTagAdminInfoDTO readEventTag(Long tagId) {
        EventTag tag = eventTagRepository.findById(tagId).orElse(null);
        if(tag == null) return null;
        return eventTagConversor.fromEntity2Dto(tag);
    }


}
