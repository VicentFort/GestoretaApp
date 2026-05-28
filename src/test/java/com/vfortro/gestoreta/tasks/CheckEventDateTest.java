package com.vfortro.gestoreta.tasks;
import static org.mockito.Mockito.*;

import com.vfortro.gestoreta.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckEventDateTest {

    @Mock private EventRepository eventRepository;

    @InjectMocks
    private CheckEventDate checkEventDate;

    @Test
    void actualizarEstados_ShouldCallCloseEndedEvents() {
        // Act
        checkEventDate.actualizarEstados();

        // Assert
        verify(eventRepository, times(1)).closeEndedEvents();
    }
}