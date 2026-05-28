package com.vfortro.gestoreta.tasks;

import static org.mockito.Mockito.*;

import com.vfortro.gestoreta.service.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalcTotalEventRevenueTest {

    @Mock private EventService eventService;

    @InjectMocks
    private CalcTotalEventRevenue calcTotalEventRevenue;

    @Test
    void getTotalRevenue_ShouldInvokeEventServiceMethod() {
        // Act
        calcTotalEventRevenue.getTotalRevenue();

        // Assert
        verify(eventService, times(1)).getTotalRevenue();
    }
}