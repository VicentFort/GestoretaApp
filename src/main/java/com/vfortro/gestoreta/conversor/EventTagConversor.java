package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.events.EventTagDto;
import com.vfortro.gestoreta.model.EventTag;
import com.vfortro.gestoreta.repository.FallaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventTagConversor {

    @Autowired
    private FallaRepository fallaRepository;
    public EventTagDto fromEntity2Dto(EventTag tag) {
        EventTagDto dto = new EventTagDto();
        dto.setId(tag.getId());
        dto.setFallaId(tag.getFalla().getId());
        dto.setName(tag.getName());
        return dto;
    }

    public EventTag fromDto2Entity(EventTagDto dto) {
        EventTag tag = new EventTag();
        tag.setId(dto.getId());
        tag.setName(dto.getName());
        if(dto.getFallaId() == null)
            throw new NullPointerException("Se debe especificar una id para la falla.");
        if(!fallaRepository.existsById(dto.getFallaId()))
            throw new EntityNotFoundException("No existe la falla en la bd.");
        tag.setFalla(fallaRepository.findFallaById(dto.getFallaId()));
        return tag;
    }
}
