package com.vfortro.gestoreta.tasks;

import com.vfortro.gestoreta.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Logger;

@Component
public class CheckEventDate {
    @Autowired
    private EventRepository eventRepository;


    @Scheduled(fixedRate = 60000)
    @Transactional
    public void actualizarEstados() {
        // SQL: UPDATE entidad SET done = true, open = false WHERE end_date < NOW() AND done = false
        eventRepository.closeEndedEvents();
        System.out.println("Eventos actualizados");
    }
}
