package com.vfortro.gestoreta.tasks;

import com.vfortro.gestoreta.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CheckEventDate {
    @Autowired
    private EventRepository eventRepository;


    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkEventDate() {
        eventRepository.closeEndedEvents();
    }
}
