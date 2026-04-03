package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.*;
import com.vfortro.gestoreta.dto.events.EventCreateDto;
import com.vfortro.gestoreta.dto.events.EventInfoDto;
import com.vfortro.gestoreta.dto.events.EventTagInfoDto;
import com.vfortro.gestoreta.dto.fallas.FallaAdminInfo;
import com.vfortro.gestoreta.dto.fallas.FallaCreateDto;
import com.vfortro.gestoreta.dto.fallas.FallaUserInfoDto;
import com.vfortro.gestoreta.dto.fallas.FallaUpdateDto;
import com.vfortro.gestoreta.dto.requests.RequestDto;
import com.vfortro.gestoreta.dto.users.UserCreateDto;
import com.vfortro.gestoreta.dto.users.UserInfoDto;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.repository.EventRepository;
import com.vfortro.gestoreta.repository.EventTagRepository;
import com.vfortro.gestoreta.repository.FallaRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class FallaService {

    @Autowired
    private FallaConversor fallaConversor;

    @Autowired
    private FallaRepository fallaRepository;

    @Autowired
    private UserConversor userConversor;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestConversor requestConversor;
    @Autowired
    private EventTagConversor eventTagConversor;
    @Autowired
    private EventTagRepository eventTagRepository;
    @Autowired
    private EventConversor eventConversor;
    @Autowired
    private EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<FallaCreateDto> getAll() {
        List<Falla> fallas = fallaRepository.findAll();
        List<FallaCreateDto> dtos = new ArrayList<FallaCreateDto>();
        for(Falla f : fallas) {
            dtos.add(fallaConversor.fromEntity2DTO(f));
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    public FallaCreateDto readFalla(String nombreFalla) {
        Falla falla = fallaRepository.findByName(nombreFalla);
        if(!Objects.nonNull(falla)) {
            return null;
        }
        return fallaConversor.fromEntity2DTO(falla);
    }

    @Transactional
    public FallaCreateDto createFalla(FallaCreateDto falla) {
        Falla saved = fallaRepository.saveAndFlush(fallaConversor.fromDto2Entity(falla));
        return fallaConversor.fromEntity2DTO(saved);
    }

    @Transactional
    public void updateFalla(FallaUpdateDto newFalla, String email) throws AccessDeniedException, NullPointerException {
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        UserCreateDto user = userService.readUser(email);
        if(user.getFallaId() == null) throw new NullPointerException("El usuario no tiene falla asignada.");
        Falla updatedFalla = fallaRepository.findFallaById(user.getFallaId());
        if(!Objects.equals(updatedFalla.getId(), userService.readUser(email).getFallaId())) throw new AccessDeniedException("Sin permiso a esta falla.");
        if(newFalla.getName() != null) updatedFalla.setName(newFalla.getName());
        if(newFalla.getCreationDate() != null) updatedFalla.setCreationDate(newFalla.getCreationDate());
        if(newFalla.getShieldUrl() != null) updatedFalla.setShieldUrl(newFalla.getShieldUrl());
        fallaRepository.saveAndFlush(updatedFalla);

    }

    @Transactional(readOnly = true)
    public FallaUserInfoDto readFalla(Long fallaId) {
        Falla falla = fallaRepository.findById(fallaId).orElse(null);
        if(falla == null) return null;
        FallaUserInfoDto result = new FallaUserInfoDto();
        result.setCreationDate(falla.getCreationDate());
        result.setFallaId(fallaId);
        result.setName(falla.getName());
        return result;
    }

    @Transactional(readOnly = true)
    public List<UserInfoDto> getUsers(String email) throws AccessDeniedException, NullPointerException, EntityNotFoundException {
        UserCreateDto dto = userService.readUser(email);
        if(dto.getFallaId() == null) throw new NullPointerException("El usuario no tiene una falla asiganda.");
        if(!fallaRepository.existsById(dto.getFallaId())) throw new EntityNotFoundException("Falla con id: " + dto.getFallaId() + " no encontrada.");
        Falla falla = fallaRepository.findFallaById(dto.getFallaId());
        if(!Objects.equals(falla.getId(), userService.readUser(email).getUserId())) throw new AccessDeniedException("Sin permiso a esta falla.");
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        Set<User> users = falla.getUsers();
        List<UserInfoDto> usersDto = new ArrayList<>();
        for(User user : users) {
            usersDto.add(userConversor.fromEntity2InfoDto(user));
        }
        return usersDto;

    }


    @Transactional(readOnly = true)
    public List<RequestDto> getRequests(String email) throws AccessDeniedException, EntityNotFoundException, NullPointerException {
        UserCreateDto dto = userService.readUser(email);
        if(dto.getFallaId() == null) throw new NullPointerException("El usuario no tiene falla asignada");
        if(!fallaRepository.existsById(dto.getFallaId())) throw new EntityNotFoundException("Falla con id: " + dto.getFallaId() + " no encontrada.");
        Falla falla = fallaRepository.findFallaById(dto.getFallaId());
        if(!dto.getFallaId().equals(userService.readUser(email).getFallaId())) throw new AccessDeniedException("Sin permiso para esta falla.");
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        List<RequestDto> reqsDto = new ArrayList<>();
        for(Request req : falla.getRequests()) {
            reqsDto.add(requestConversor.fromEntity2Dto(req));
        }
        return reqsDto;
    }

    @Transactional
    public void addEventTag(String email, String name) throws AccessDeniedException, EntityNotFoundException, EntityExistsException {
        UserCreateDto userDto = userService.readUser(email);
        if(userDto.getFallaId()== null) throw new EntityNotFoundException("El usuario no tiene una falla asociada.");
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin acceso.");
        Falla falla = fallaRepository.findFallaById(userDto.getFallaId());
        if(eventTagRepository.existsEventTagByNameAndFalla(name, falla)) throw new EntityExistsException("Ya existe una etiqueta: " + name + " para la falla: " + falla.getName());
        EventTag tagSave = new EventTag();
        tagSave.setFalla(falla);
        tagSave.setName(name);
        eventTagRepository.saveAndFlush(tagSave);
    }
    @Transactional(readOnly = true)
    public List<EventTagInfoDto> getEventTags(String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        UserCreateDto user = userService.readUser(email);
        Falla falla = fallaRepository.findFallaById(user.getFallaId());
        List<EventTagInfoDto> tags = new ArrayList<>();
        for(EventTag tag : falla.getEventTags()) {
            tags.add(eventTagConversor.fromEntity2Dto(tag));
        }
        return tags;

    }

    @Transactional
    public List<EventInfoDto> getEvents(String email) throws AccessDeniedException{
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin acceso.");
        UserCreateDto dto = userService.readUser(email);
        Falla falla = fallaRepository.findFallaById(dto.getFallaId());
        List<EventInfoDto> result = new ArrayList<>();
        for(Event event : falla.getEvents()) {
            Event openEvent = checkOpenEvent(event);
            result.add(eventConversor.fromEntity2InfoDto(openEvent));
        }
        return result;

    }

    @Transactional
    public Event checkOpenEvent(Event event) {
        if(event.getEndDate().isAfter(LocalDateTime.now()) && event.getEndHour().isAfter(LocalTime.now())) {
            event.setOpen(false);
            event.setDone(true);
            return eventRepository.saveAndFlush(event);
        }
        return event;

    }

    public List<EventCreateDto> getActiveEvents(String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin acceso.");
        Falla falla = fallaRepository.findFallaById(userService.readUser(email).getFallaId());
        List<EventCreateDto> result = new ArrayList<>();
        for(Event event : falla.getEvents()) {
            Event openEvent = checkOpenEvent(event);
            if(!openEvent.getDone()) {
                result.add(eventConversor.fromEntity2Dto(openEvent));
            }
        }
        return result;
    }

    public FallaAdminInfo getFallaInfo(String email) throws AccessDeniedException {
        UserCreateDto infoDto = userService.readUser(email);
        if(infoDto.getFallaId()==null) throw new EntityNotFoundException("No existe la falla del usuario.");
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        Falla falla = fallaRepository.findFallaById(infoDto.getFallaId());
        return fallaConversor.fromEntity2AdminInfo(falla);

    }

    public void editAdminAccess(Long userId, Boolean access, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email) || !userService.checkOtherAccess(email)) throw new AccessDeniedException("Sense permissos.");
        userService.editAdminAccess(userId, access);
    }

    public void deleteEventTag(Long tagId, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        eventTagRepository.deleteById(tagId);
    }
}
