package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.AssistConversor;
import com.vfortro.gestoreta.dto.assists.AssistDto;
import com.vfortro.gestoreta.model.Assist;
import com.vfortro.gestoreta.model.Event;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.repository.AssistRepository;
import com.vfortro.gestoreta.repository.EventRepository;
import com.vfortro.gestoreta.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssistService {
    @Autowired
    private AssistRepository assistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AssistConversor assistConversor;
    @Autowired
    private UserService userService;

    @Transactional(readOnly = true)
    public AssistDto readAssist(String email, Long eventId) {
        Assist assist = assistRepository.findByUserEmailAndEventId(email, eventId);
        if(assist == null) return null;
        return assistConversor.formEntity2Dto(assist);
    }

    @Transactional
    public AssistDto createAssist(AssistDto assist) throws EntityExistsException {
        Assist saved = assistRepository.saveAndFlush(assistConversor.fromDto2Entity(assist));
        return assistConversor.formEntity2Dto(saved);
    }

    @Transactional
    public AssistDto createAssist(String email, Long eventId) {
        AssistDto dto = new AssistDto();
        dto.setUserId(userService.readUser(email).getUserId());
        dto.setEventId(eventId);
        Assist saved = assistRepository.saveAndFlush(assistConversor.fromDto2Entity(dto));
        return assistConversor.formEntity2Dto(saved);
    }

    @Transactional
    public void deleteAssist(Long assistId) {
        assistRepository.deleteById(assistId);
    }
}
