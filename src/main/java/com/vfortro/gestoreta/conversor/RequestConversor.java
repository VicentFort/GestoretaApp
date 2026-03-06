package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.requests.RequestCreateDto;
import com.vfortro.gestoreta.dto.requests.RequestDto;
import com.vfortro.gestoreta.dto.requests.RequestInfoDto;
import com.vfortro.gestoreta.model.Request;
import com.vfortro.gestoreta.repository.FallaRepository;
import com.vfortro.gestoreta.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestConversor {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FallaRepository fallaRepository;

    public RequestDto fromEntity2Dto(Request request) {
        RequestDto dto = new RequestDto();
        dto.setRequestId(request.getId());
        dto.setIdUser(request.getUser().getId());
        dto.setIdFalla(request.getFalla().getId());
        dto.setMessage(request.getMessage());
        dto.setAproved(request.getAproved());
        dto.setReply(request.getReply());
        return dto;
    }

    public RequestInfoDto fromEntity2InfoDto(Request req) {
        RequestInfoDto dto = new RequestInfoDto();
        dto.setUsername(req.getUser().getName());
        dto.setFallaName(req.getFalla().getName());
        dto.setReply(req.getReply());
        dto.setRequestId(req.getId());
        dto.setMessage(req.getMessage());
        dto.setAproved(req.getAproved());
        return dto;
    }

    public Request fromDto2Entity(RequestCreateDto dto) throws NullPointerException, EntityNotFoundException {
        Request req = new Request();

        if(dto.getIdUser() == null) throw new NullPointerException("La solicitud debe tener una id de usuario.");
        if(!userRepository.existsById(dto.getIdUser())) throw new EntityNotFoundException("El usuario con id: " + dto.getIdUser() + " no existe.");
        req.setUser(userRepository.findUserById(dto.getIdUser()));

        if(dto.getIdFalla() == null) throw new NullPointerException("La solicitud debe tener una id de falla.");
        if(!fallaRepository.existsById(dto.getIdFalla())) throw new EntityNotFoundException("La falla con id: " + dto.getIdFalla() + " no existe.");
        req.setFalla(fallaRepository.findFallaById(dto.getIdFalla()));

        if(dto.getMessage().isBlank()) throw new NullPointerException("El mensaje debe tener contenido.");
        req.setMessage(dto.getMessage());
        return req;
    }

    public Request fromDto2Entity(RequestDto dto) throws NullPointerException, EntityNotFoundException {
        Request req = new Request();
        req.setId(dto.getRequestId());

        if(dto.getIdUser() == null) throw new NullPointerException("La solicitud debe tener una id de usuario.");
        if(!userRepository.existsById(dto.getIdUser())) throw new EntityNotFoundException("El usuario con id: " + dto.getIdUser() + " no existe.");
        req.setUser(userRepository.findUserById(dto.getIdUser()));

        if(dto.getIdFalla() == null) throw new NullPointerException("La solicitud debe tener una id de falla.");
        if(!fallaRepository.existsById(dto.getIdFalla())) throw new EntityNotFoundException("La falla con id: " + dto.getIdFalla() + " no existe.");
        req.setFalla(fallaRepository.findFallaById(dto.getIdFalla()));

        if(dto.getMessage().isBlank()) throw new NullPointerException("El mensaje debe tener contenido.");
        req.setMessage(dto.getMessage());

        req.setAproved(dto.getAproved());
        req.setReply(dto.getReply());

        return req;
    }
}
