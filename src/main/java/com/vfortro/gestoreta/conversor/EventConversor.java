package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.EventCreateDto;
import com.vfortro.gestoreta.model.Event;
import com.vfortro.gestoreta.model.EventTag;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.repository.EventTagRepository;
import com.vfortro.gestoreta.repository.FallaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventConversor {

    @Autowired
    private FallaRepository fallaRepository;
    @Autowired
    private EventTagRepository tagRepository;

    public EventCreateDto fromEntity2Dto(Event event) {
        EventCreateDto dto = new EventCreateDto();
        dto.setId(event.getId());
        dto.setPublicField(event.getPublicField());
        dto.setDone(event.getDone());
        dto.setPrice(event.getPrice());
        dto.setDescription(event.getDescription());
        dto.setMaxPeople(event.getMaxPeople());
        dto.setDate(event.getDate());
        dto.setFallaId(event.getFalla().getId());
        dto.setTagId(event.getEventTag().getId());
        dto.setTitle(event.getTitle());
        return dto;
    }

    public Event fromDto2Entity(EventCreateDto dto) {
        Event event = new Event();
        event.setId(dto.getId());
        event.setPublicField(dto.getPublicField());
        event.setDone(dto.getDone());
        event.setPrice(dto.getPrice());
        event.setPrice(dto.getPrice());
        event.setDescription(dto.getDescription());
        event.setMaxPeople(dto.getMaxPeople());
        event.setDate(dto.getDate());
        event.setTitle(dto.getTitle());

        if(dto.getTagId() == null) throw new NullPointerException("El evento debe tener una id de etiqueta de evento");
        if(!tagRepository.existsById(dto.getTagId())) throw new EntityNotFoundException("La etiqueta asignada al evento no existe en la base de datos.");
        EventTag tag = tagRepository.findById(dto.getTagId()).orElse(null);

        if(dto.getFallaId() == null) throw new NullPointerException("El evento debe tener una id de falla");
        if(!fallaRepository.existsById(dto.getFallaId())) throw new EntityNotFoundException("La falla asignada al evento no existe");
        Falla falla = fallaRepository.findById(dto.getFallaId()).orElse(null);

        event.setEventTag(tag);
        event.setFalla(falla);

        return event;
    }
}
