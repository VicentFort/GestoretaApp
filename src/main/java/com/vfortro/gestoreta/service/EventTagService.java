package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.EventTagConversor;
import com.vfortro.gestoreta.dto.EventTagDto;
import com.vfortro.gestoreta.model.EventTag;
import com.vfortro.gestoreta.repository.EventTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventTagService {
    @Autowired
    private EventTagRepository eventTagRepository;

    @Autowired
    private EventTagConversor eventTagConversor;

    public EventTagDto readEventTag(Long tagId) {
        EventTag tag = eventTagRepository.findById(tagId).orElse(null);
        if(tag == null) return null;
        return eventTagConversor.fromEntity2Dto(tag);
    }
}
