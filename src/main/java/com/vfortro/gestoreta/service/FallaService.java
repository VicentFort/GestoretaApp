package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.FallaConversor;
import com.vfortro.gestoreta.conversor.RequestConversor;
import com.vfortro.gestoreta.conversor.UserConversor;
import com.vfortro.gestoreta.dto.*;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.Request;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.repository.FallaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
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
        Falla saved = fallaRepository.save(fallaConversor.fromDto2Entity(falla));
        return fallaConversor.fromEntity2DTO(saved);
    }

    @Transactional
    public void updateFalla(FallaUpdateDto newFalla, Long idFalla, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");

        Falla updatedFalla = fallaRepository.findFallaById(idFalla);
        if(!Objects.equals(updatedFalla.getId(), userService.readUser(email).getFallaId())) throw new AccessDeniedException("Sin permiso a esta falla.");
        if(newFalla.getName() != null) updatedFalla.setName(newFalla.getName());
        if(newFalla.getCreationDate() != null) updatedFalla.setCreationDate(newFalla.getCreationDate());
        if(newFalla.getShieldUrl() != null) updatedFalla.setShieldUrl(newFalla.getShieldUrl());
        fallaRepository.save(updatedFalla);

    }

    @Transactional(readOnly = true)
    public FallaInfoDto readFalla(Long fallaId) {
        Falla falla = fallaRepository.findById(fallaId).orElse(null);
        if(falla == null) return null;
        FallaInfoDto result = new FallaInfoDto();
        result.setCreationDate(falla.getCreationDate());
        result.setFallaId(fallaId);
        result.setName(falla.getName());
        return result;
    }

    @Transactional(readOnly = true)
    public List<UserCreateDto> getUsers(Long fallaId, String email) throws AccessDeniedException {
        if(!fallaRepository.existsById(fallaId)) return null;
        Falla falla = fallaRepository.findFallaById(fallaId);
        if(!Objects.equals(falla.getId(), userService.readUser(email).getUserId())) throw new AccessDeniedException("Sin permiso a esta falla.");
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        Set<User> users = falla.getUsers();
        List<UserCreateDto> usersDto = new ArrayList<>();
        for(User user : users) {
            usersDto.add(userConversor.fromEntity2Dto(user));
        }
        return usersDto;

    }


    @Transactional(readOnly = true)
    public List<RequestDto> getRequests(Long fallaId, String email) throws AccessDeniedException {
        if(!fallaRepository.existsById(fallaId)) return null;
        Falla falla = fallaRepository.findFallaById(fallaId);
        if(!fallaId.equals(userService.readUser(email).getFallaId())) throw new AccessDeniedException("Sin permiso para esta falla.");
        if(!userService.checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        Set<Request> reqs = falla.getRequests();
        List<RequestDto> reqsDto = new ArrayList<>();
        for(Request req : reqs) {
            reqsDto.add(requestConversor.fromEntity2Dto(req));
        }
        return reqsDto;
    }
}
