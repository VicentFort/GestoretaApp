package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.assists.AssistDto;
import com.vfortro.gestoreta.model.Assist;
import com.vfortro.gestoreta.repository.AssistRepository;
import com.vfortro.gestoreta.repository.EventRepository;
import com.vfortro.gestoreta.repository.FallaRepository;
import com.vfortro.gestoreta.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssistConversor {
    @Autowired
    private FallaRepository fallaRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AssistRepository assistRepository;

    public AssistDto formEntity2Dto(Assist assist) {
        AssistDto dto = new AssistDto();
        dto.setAssistId(assist.getId());
        dto.setUserId(assist.getUser().getId());
        dto.setEventId(assist.getEvent().getId());
        dto.setPaid(assist.getPaid());
        return dto;
    }

    public Assist fromDto2Entity(AssistDto dto) {
        Assist assist = new Assist();
        assist.setId(dto.getAssistId());

        if(dto.getUserId() == null) throw new NullPointerException("La asistencia debe tener asignada una id de usuario.");
        if(!userRepository.existsById(dto.getUserId())) throw new EntityNotFoundException("El usuario asignado a la asistencia no existe en la base de datos.");
        assist.setUser(userRepository.findUserById(dto.getUserId()));

        if(dto.getEventId() == null) throw new NullPointerException("La asistencia debe tener asignada una id de evento.");
        if(!eventRepository.existsById(dto.getEventId())) throw new EntityNotFoundException("El evento asignado a la asistencia no existe en la base de datos.");
        assist.setEvent(eventRepository.findEventById(dto.getEventId()));

        if(assistRepository.existsByUserIdAndEventId(assist.getUser().getId(), assist.getEvent().getId())) throw new EntityExistsException("La asisntencia ya existe");
        assist.setPaid(dto.getPaid());
        return assist;
    }
}
