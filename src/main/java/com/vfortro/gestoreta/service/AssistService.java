package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.AssistConversor;
import com.vfortro.gestoreta.dto.AssistDto;
import com.vfortro.gestoreta.model.Assist;
import com.vfortro.gestoreta.model.Event;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.repository.AssistRepository;
import com.vfortro.gestoreta.repository.EventRepository;
import com.vfortro.gestoreta.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public AssistDto readAssist(Long userId, Long eventId) {
        Assist assist = assistRepository.findByUserIdAndEventId(userId, eventId).orElse(null);
        if(assist == null) return null;
        return assistConversor.formEntity2Dto(assist);
    }

    public void createAssist(AssistDto assist) {
        assistRepository.save(assistConversor.fromDto2Entity(assist));
    }

    public AssistDto createAssist(Long userId, Long eventId) {
        User user = userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);
        Assist toSave = new Assist();
        toSave.setUser(user); toSave.setEvent(event);
        return assistConversor.formEntity2Dto(assistRepository.save(toSave));
    }

    public void deleteAssist(Long assistId) {
        assistRepository.deleteById(assistId);
    }
}
