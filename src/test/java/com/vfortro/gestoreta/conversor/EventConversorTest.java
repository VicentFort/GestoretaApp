package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.events.EventCreateDTO;
import com.vfortro.gestoreta.model.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventConversorTest {

    @InjectMocks
    private EventConversor eventConversor;

    @Test
    void fromEntity2Dto_ok() {

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Mascletà");

        EventCreateDTO dto = eventConversor.fromEntity2Dto(event);

        assertNotNull(dto);
        assertEquals(event.getId(), dto.getId());
    }

    @Test
    void fromDto2Entity_ok() {

        EventCreateDTO dto = new EventCreateDTO();
        dto.setTitle("Mascletà");

        Event event = eventConversor.fromDto2Entity(dto);

        assertNotNull(event);
        assertEquals(dto.getTitle(), event.getTitle());
    }
}