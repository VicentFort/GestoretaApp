package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.RequestConversor;
import com.vfortro.gestoreta.dto.RequestCreateDto;
import com.vfortro.gestoreta.dto.RequestDto;
import com.vfortro.gestoreta.model.Request;
import com.vfortro.gestoreta.repository.RequestRepository;
import com.vfortro.gestoreta.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestConversor requestConversor;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public RequestDto readRequest(Long requestId) {
        Request req = requestRepository.findById(requestId).orElse(null);
        if(req == null) return null;
        return requestConversor.fromEntity2Dto(req);
    }

    @Transactional
    public RequestDto createRequest(RequestCreateDto dto) throws NullPointerException, EntityNotFoundException, IllegalAccessException {
        Request toSave = requestConversor.fromDto2Entity(dto);
        if(Objects.nonNull(toSave.getUser().getFalla()))
            throw new IllegalAccessException("El usuario con id:" + dto.getIdUser() + " ya está en una falla.");
        Request saved = requestRepository.saveAndFlush(toSave);
        return requestConversor.fromEntity2Dto(saved);
    }

    @Transactional
    public void updateRequest(RequestDto dto) {
        Request req = requestRepository.findById(dto.getRequestId()).map(
                request -> {
                    request.setAproved(dto.getAproved());
                    request.setReply(dto.getReply());
                    return requestRepository.saveAndFlush(request);
                }
        ).orElse(null);
    }
}
