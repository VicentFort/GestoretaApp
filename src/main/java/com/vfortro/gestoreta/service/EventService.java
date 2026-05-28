package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.AssistConversor;
import com.vfortro.gestoreta.conversor.EventConversor;
import com.vfortro.gestoreta.conversor.EventTagConversor;
import com.vfortro.gestoreta.dto.assists.AssistDTO;
import com.vfortro.gestoreta.dto.events.EventCreateDTO;
import com.vfortro.gestoreta.dto.events.EventTagAdminInfoDTO;
import com.vfortro.gestoreta.dto.events.EventUpdateDTO;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.model.enums.UserNotificationType;
import com.vfortro.gestoreta.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    @Autowired
    private UserNotificationRepository notificationRepository;

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

    @Transactional(readOnly = true)
    public List<Event> readEvents() {
        return eventRepository.findAll();
    }


    @Transactional
    public EventCreateDTO createEvent(@Valid EventCreateDTO event, String email) throws EntityNotFoundException, AccessDeniedException, IllegalAccessException {
        if(!eventTagRepository.existsById(event.getTagId())) {
            throw new EntityNotFoundException("La etiqueta a la que se está asociando el evento: " + event.getTitle() + " no existe");
        }
        if(!Objects.equals(event.getFallaId(), userService.readUser(email).getFallaId())) throw new AccessDeniedException("Sin permiso para esta falla.");
        if(!userService.checkManagerAccess(email)) throw new AccessDeniedException("Sin permiso.");
        Event toSave = eventConversor.fromDto2Entity(event);
        Event saved = eventRepository.saveAndFlush(toSave);

        if(event.getAttendants() != null && !event.getAttendants().isEmpty()) {
            for (Long userId : event.getAttendants()) {
                Attendant attendant = new Attendant();
                attendant.setEvent(saved);
                attendant.setFalla(saved.getFalla());
                User user = userService.readUserAsEntity(userId);
                attendant.setUser(user);
                UserNotification notification = new UserNotification();
                String message = "La teua falla t'ha seleccionat per a gestionar l'esdeveniment: " + saved.getTitle() + " ja que tens dispossició a l'etiqueta: " + saved.getEventTag().getName();
                notification.setMessage(message);
                notification.setUser(user);
                notification.setEvent(saved);
                notification.setType(UserNotificationType.EVENT_ATTENDANT_PETITION);
                notification.setRead(false);
                notification.setFalla(saved.getFalla());
                notification.setDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
                notificationRepository.save(notification);
                attendantRepository.save(attendant);
            }

        }
        return eventConversor.fromEntity2Dto(saved);
    }

    @Transactional
    public void deleteEvent(Long eventId, String email) throws AccessDeniedException, EntityNotFoundException, IllegalAccessException {
        Event event = eventRepository.findById(eventId).orElseThrow( () -> new EntityNotFoundException("No existeix l'esdeveniment amb id: " + eventId));
        if(!userService.checkManagerAccess(email)) throw new AccessDeniedException("Sense permís");
        if(!Objects.equals(event.getFalla().getId(), userService.readUser(email).getFallaId())) throw new AccessDeniedException("Sense permís");
        eventRepository.delete(event);
    }

    @Transactional
    public EventCreateDTO updateEvent(EventUpdateDTO newEvent, String email) throws AccessDeniedException, EntityNotFoundException, IllegalAccessException {
        if(!userService.checkManagerAccess(email)) throw new AccessDeniedException("Sense permís");
        Event updatedEvent = eventRepository.findById(newEvent.getEventId()).orElseThrow(() -> new EntityNotFoundException("No existeix l'esdeveniment amb id: " + newEvent.getEventId()));
        if(!Objects.equals(updatedEvent.getFalla().getId(), userService.readUser(email).getFallaId())) {
           throw new AccessDeniedException("Sense permís");
        }
        if(newEvent.getEndHour().isBefore(newEvent.getStartHour()) || newEvent.getStartHour().isAfter(newEvent.getEndHour())) {
            throw new IllegalStateException("La data d'inici es posterior a la data de fi");
        }

        if(newEvent.getPublicField() != null) updatedEvent.setPublicField(newEvent.getPublicField());
        if(newEvent.getTitle() != null) updatedEvent.setTitle(newEvent.getTitle());
        if(newEvent.getDescription() != null) updatedEvent.setDescription(newEvent.getDescription());
        if(newEvent.getDate() != null) updatedEvent.setDate(LocalDateTime.of(newEvent.getDate(),newEvent.getStartHour()));
        if(newEvent.getTagId() != null && eventTagRepository.existsById(newEvent.getTagId())) {
            updatedEvent.setEventTag(eventTagRepository.findTagById(newEvent.getTagId()));
        }
        if(newEvent.getImage() != null) {
            System.out.println("SAME IMAGE?: "+ Arrays.equals(newEvent.getImage(), updatedEvent.getImageContent()));
            updatedEvent.setImageContent(newEvent.getImage());
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
                User user = userService.readUserAsEntity(userId);
                if(attendantRepository.existsByUser_IdAndEvent_Id(userId, newEvent.getEventId())) {
                    break;
                }
                if (user != null) {
                    Attendant attendant = new Attendant();
                    attendant.setUser(user);
                    attendant.setEvent(updatedEvent);
                    attendant.setFalla(updatedEvent.getFalla());
                    UserNotification notification = new UserNotification();
                    String message = "La teua falla t'ha seleccionat per a gestionar l'esdeveniment: " + updatedEvent.getTitle() + " ja que tens dispossició a l'etiqueta: " + updatedEvent.getEventTag().getName();
                    notification.setMessage(message);
                    notification.setType(UserNotificationType.EVENT_ATTENDANT_PETITION);
                    notification.setUser(user);
                    notification.setEvent(updatedEvent);
                    notification.setRead(false);
                    notification.setFalla(updatedEvent.getFalla());
                    notification.setDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
                    notificationRepository.save(notification);
                    // Añadimos a la colección del evento
                    updatedEvent.getAttendants().add(attendant);
                }
            }

        }
        if(updatedEvent.getPrice() != null) {
            List<Assist> updatedAssists = updatedEvent.getAssists().stream().map(assist -> {
                if(updatedEvent.getPrice() > 0) {
                    if(assist.getPaid() == null || !assist.getPaid()) {
                        assist.setPaid(false);
                    }
                };
                if(updatedEvent.getPrice() <= 0) {
                    if(assist.getPaid() != null && !assist.getPaid()) {
                        assist.setPaid(null);
                    }
                }
                return assist;
            }).toList();
            assistRepository.saveAll(updatedAssists);
        }
        Event saved = eventRepository.save(updatedEvent);

        return eventConversor.fromEntity2Dto(saved);
    }

    @Transactional(readOnly = true)
    public Event readEventAsEntity(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("No existeix l'esdeveniment amb id: " + eventId));
    }

    @Transactional(readOnly = true)
    public AssistDTO readAssist(String email, Long eventId) {
        Assist assist = assistRepository.findByUserEmailAndEventId(email, eventId);
        if(assist == null) return null;
        return assistConversor.formEntity2Dto(assist);
    }


    @Transactional
    public AssistDTO createAssist(String email, Long eventId) throws EntityNotFoundException, EntityExistsException {

        Event event = readEventAsEntity(eventId);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("L'usuari amb email: " + email + " no existeix."));
        if(assistRepository.existsByUserIdAndEventId(user.getId(), eventId)) {
            throw new EntityExistsException("Ja existeix la assistència");
        };

        AssistDTO dto = new AssistDTO();
        dto.setUserId(user.getId());
        dto.setEventId(eventId);
        if(event.getPrice() > 0) {
            dto.setPaid(true);
        }

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

    @Transactional
    public void payAssist(Long userId, Long eventId) throws EntityNotFoundException, IllegalStateException{
        Assist assist = assistRepository.findByUserIdAndEventId(userId,eventId).orElseThrow(() -> new EntityNotFoundException("La assistència de l'usuari i esdeveniment indicats no existeix"));
        if(assist.getPaid() == null) throw new IllegalStateException("L'esdeveniment no s'ha de pagar");
        if(assist.getPaid()) throw new IllegalStateException("L'usuari ja havia pagat l'esdeveniment");
        assist.setPaid(true);
        assistRepository.saveAndFlush(assist);
    }


    @Transactional
    public void getTotalRevenue() {
        AtomicInteger processedEvents = new AtomicInteger();
        List<Event> events = eventRepository.findByTotalRevenueIsNotNullAndInactiveAndWithPrice();
        if(!events.isEmpty()) {
            List<Event> eventList = events.stream().map(event -> {
                List<Assist> assists = event.getAssists().stream().filter(assist -> assist.getPaid() == true).toList();
                event.setTotalRevenue(assists.size() * event.getPrice());
                processedEvents.getAndIncrement();
                return event;
            }).toList();
            eventRepository.saveAll(eventList);
            System.out.println("S'han calculat el retorn de: " + processedEvents.get() + " esdeveniments");
        }

    }


}
