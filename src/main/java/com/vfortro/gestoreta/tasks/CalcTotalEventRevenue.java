package com.vfortro.gestoreta.tasks;

import com.vfortro.gestoreta.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CalcTotalEventRevenue {

    @Autowired
    private EventService eventService;

    @Scheduled(fixedRate = 60000)
    public void getTotalRevenue() {
        eventService.getTotalRevenue();
    }
}
