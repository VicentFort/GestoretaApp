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
    private RequestConversor requestConversor;

    public List<FallaCreateDto> getAll() {
        List<Falla> fallas = fallaRepository.findAll();
        List<FallaCreateDto> dtos = new ArrayList<FallaCreateDto>();
        for(Falla f : fallas) {
            dtos.add(fallaConversor.fromEntity2DTO(f));
        }
        return dtos;
    }

    public FallaCreateDto readFalla(String nombreFalla) {
        Falla falla = fallaRepository.findByName(nombreFalla);
        if(!Objects.nonNull(falla)) {
            return null;
        }
        return fallaConversor.fromEntity2DTO(falla);
    }

    public FallaCreateDto createFalla(FallaCreateDto falla) {
        Falla saved = fallaRepository.save(fallaConversor.fromDto2Entity(falla));
        return fallaConversor.fromEntity2DTO(saved);
    }

    public FallaCreateDto updateFalla(FallaUpdateDto newFalla, Long idFalla) {
        Falla updatedFalla = fallaRepository.findById(idFalla).map(
                falla -> {
                    if(newFalla.getName() != null) falla.setName(newFalla.getName());
                    if(newFalla.getCreationDate() != null) falla.setCreationDate(newFalla.getCreationDate());
                    if(newFalla.getShieldUrl() != null) falla.setShieldUrl(newFalla.getShieldUrl());
                    return fallaRepository.save(falla);
                }).orElseGet(() -> {
                    return null;
                });
        if(updatedFalla == null) return null;
        return fallaConversor.fromEntity2DTO(updatedFalla);
    }

    public FallaCreateDto readFalla(Long fallaId) {
        Falla falla = fallaRepository.findById(fallaId).orElse(null);
        if(falla == null) return null;
        return fallaConversor.fromEntity2DTO(falla);
    }

    public List<UserCreateDto> getUsers(Long fallaId) {
        if(!fallaRepository.existsById(fallaId)) return null;
        Falla falla = fallaRepository.findFallaById(fallaId);
        Set<User> users = falla.getUsers();
        List<UserCreateDto> usersDto = new ArrayList<>();
        for(User user : users) {
            usersDto.add(userConversor.fromEntity2Dto(user));
        }
        return usersDto;

    }


    public List<RequestDto> getRequests(Long fallaId) {
        if(!fallaRepository.existsById(fallaId)) return null;
        Falla falla = fallaRepository.findFallaById(fallaId);
        Set<Request> reqs = falla.getRequests();
        List<RequestDto> reqsDto = new ArrayList<>();
        for(Request req : reqs) {
            reqsDto.add(requestConversor.fromEntity2Dto(req));
        }
        return reqsDto;
    }
}
