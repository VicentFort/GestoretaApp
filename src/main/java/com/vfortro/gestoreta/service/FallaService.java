package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.*;
import com.vfortro.gestoreta.dto.fallas.info.FallaAdminInfoDTO;
import com.vfortro.gestoreta.dto.fallas.FallaCreateDTO;
import com.vfortro.gestoreta.dto.fallas.info.FallaUserInfoDTO;
import com.vfortro.gestoreta.dto.fallas.FallaUpdateDTO;
import com.vfortro.gestoreta.dto.users.UserCreateDTO;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.repository.EventTagRepository;
import com.vfortro.gestoreta.repository.FallaRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.*;

@Service
public class FallaService {

    //REPOSITORIES
    @Autowired
    private FallaRepository fallaRepository;
    @Autowired
    private EventTagRepository eventTagRepository;

    //CONVERSORS
    @Autowired
    private FallaConversor fallaConversor;

    //SERVICES
    @Autowired
    private UserService userService;



    @Transactional
    public FallaCreateDTO createFalla(FallaCreateDTO falla) {
        Falla saved = fallaRepository.saveAndFlush(fallaConversor.fromDto2Entity(falla));
        return fallaConversor.fromEntity2DTO(saved);
    }

    @Transactional
    public void updateFalla(FallaUpdateDTO newFalla, String email) throws AccessDeniedException, EntityNotFoundException, IllegalAccessException {
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        UserCreateDTO user = userService.readUser(email);
        if(user.getFallaId() == null) throw new NullPointerException("El usuario no tiene falla asignada.");
        Falla updatedFalla = fallaRepository.findFallaById(user.getFallaId());
        if(!Objects.equals(updatedFalla.getId(), userService.readUser(email).getFallaId())) throw new AccessDeniedException("Sin permiso a esta falla.");
        if(newFalla.getName() != null) updatedFalla.setName(newFalla.getName());
        if(newFalla.getCreationDate() != null) updatedFalla.setCreationDate(newFalla.getCreationDate());
        if(newFalla.getShieldUrl() != null) updatedFalla.setShieldUrl(newFalla.getShieldUrl());
        fallaRepository.saveAndFlush(updatedFalla);

    }

    @Transactional(readOnly = true)
    public FallaUserInfoDTO readFalla(Long fallaId) {
        Falla falla = fallaRepository.findById(fallaId).orElse(null);
        if(falla == null) return null;
        FallaUserInfoDTO result = new FallaUserInfoDTO();
        result.setCreationDate(falla.getCreationDate());
        result.setFallaId(fallaId);
        result.setName(falla.getName());
        return result;
    }

    @Transactional
    public void addEventTag(String email, String name) throws AccessDeniedException, EntityNotFoundException, EntityExistsException, IllegalAccessException {
        UserCreateDTO userDto = userService.readUser(email);
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
    public FallaAdminInfoDTO getFallaInfo(String email) throws AccessDeniedException, IllegalAccessException {
        UserCreateDTO infoDto = userService.readUser(email);
        if(infoDto.getFallaId()==null) throw new EntityNotFoundException("No existe la falla del usuario.");
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        Falla falla = fallaRepository.findFallaById(infoDto.getFallaId());
        return fallaConversor.fromEntity2AdminInfo(falla);

    }

    @Transactional
    public void editAdminAccess(Long userId, Boolean access, String email) throws AccessDeniedException, IllegalAccessException {
        if(!userService.checkAdminAccess(email) || !userService.checkOtherAccess(email)) throw new AccessDeniedException("Sense permissos.");
        userService.editAdminAccess(userId, access);
    }

    @Transactional
    public void deleteEventTag(Long tagId, String email) throws AccessDeniedException, IllegalAccessException {
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        eventTagRepository.deleteById(tagId);
    }
}
