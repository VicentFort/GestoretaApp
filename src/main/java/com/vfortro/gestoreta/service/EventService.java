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
import jakarta.persistence.EntityExistsException;
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
    public EventCreateDTO createEvent(@Valid EventCreateDTO event, String email) throws EntityNotFoundException, AccessDeniedException, IllegalAccessException {
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
    public void deleteEvent(Long eventId, String email) throws AccessDeniedException, EntityNotFoundException, IllegalAccessException {
        Event event = eventRepository.findById(eventId).orElseThrow( () -> new EntityNotFoundException("No existeix l'esdeveniment amb id: " + eventId));
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sense permís");
        if(!Objects.equals(event.getFalla().getId(), userService.readUser(email).getFallaId())) throw new AccessDeniedException("Sense permís");
        eventRepository.delete(event);
    }

    @Transactional
    public EventCreateDTO updateEvent(EventUpdateDTO newEvent, String email) throws AccessDeniedException, EntityNotFoundException, IllegalAccessException {
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sense permís");
        Event updatedEvent = eventRepository.findById(newEvent.getEventId()).orElseThrow(() -> new EntityNotFoundException("No existeix l'esdeveniment amb id: " + newEvent.getEventId()));
        if(!Objects.equals(updatedEvent.getFalla().getId(), userService.readUser(email).getFallaId())) {
           throw new AccessDeniedException("Sense permís");
        }
        if(newEvent.getEndHour().isBefore(newEvent.getStartHour()) || newEvent.getStartHour().isAfter(newEvent.getEndHour())) {
            throw new IllegalStateException("La data d'inici es posterior a la data de fi");
        }

        if(newEvent.getDone() != null) updatedEvent.setActive(newEvent.getDone());
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
    public AssistDTO createAssist(String email, Long eventId) throws EntityNotFoundException, EntityExistsException {
        if(!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("L'esdeveniment amb id: " + eventId + " no existex");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("L'usuari amb email: " + email + " no existeix."));
        if(assistRepository.existsByUserIdAndEventId(user.getId(), eventId)) {
            throw new EntityExistsException("Ja existeix la assistència");
        };

        AssistDTO dto = new AssistDTO();
        dto.setUserId(user.getId());
        dto.setEventId(eventId);

        Assist saved = assistRepository.saveAndFlush(assistConversor.fromDto2Entity(dto));
        return assistConversor.formEntity2Dto(saved);
    }

    @Transactional
    public void deleteAssist(Long assistId, String email) throws EntityNotFoundException, IllegalStateException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("No exiteix l'usuari amb email: " + email));
        Assist assist = assistRepository.findById(assistId).orElseThrow(() -> new EntityNotFoundException("No existeix l'assistència"));
        if(!Objects.equals(assist.getUser().getId(), user.getId())) {
            throw new IllegalStateException("L'assistència no pertany a l'usuari indicat");
        }
        assistRepository.delete(assist);
    }

    @Transactional(readOnly = true)
    public EventTagAdminInfoDTO readEventTag(Long tagId) {
        EventTag tag = eventTagRepository.findById(tagId).orElse(null);
        if(tag == null) return null;
        return eventTagConversor.fromEntity2Dto(tag);
    }


}
