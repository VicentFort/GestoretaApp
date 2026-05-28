package com.vfortro.gestoreta.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vfortro.gestoreta.dto.assists.AssistDTO;
import com.vfortro.gestoreta.dto.events.EventCreateDTO;
import com.vfortro.gestoreta.dto.events.EventUpdateDTO;
import com.vfortro.gestoreta.model.Event;
import com.vfortro.gestoreta.repository.EventRepository;
import com.vfortro.gestoreta.service.EventService;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.nio.file.AccessDeniedException;
import java.time.LocalTime;
import java.util.List;

@ExtendWith(MockitoExtension.class) // Usamos Mockito puro, cero Spring anotaciones conflictivas
class EventControllerTest {

    @Mock private EventService eventService;
    @Mock private EventRepository eventRepository;
    @Mock private Authentication authentication;

    @InjectMocks
    private EventController eventController;

    private final String userEmail = "manager@gestoreta.com";

    @BeforeEach
    void setUp() {
        lenient().when(authentication.getName()).thenReturn(userEmail);
    }

    // ==========================================
    // ENDPOINT: GET /event/testRevenue
    // ==========================================
    @Test
    void getTestRevenue_Success() {
        // Arrange
        when(eventRepository.findByTotalRevenueIsNotNullAndInactiveAndWithPrice())
                .thenReturn(List.of(new Event(), new Event()));

        // Act
        ResponseEntity<?> response = eventController.getTestRevenue(authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody());
        verify(eventService, times(1)).getTotalRevenue();
    }

    // ==========================================
    // ENDPOINT: POST /event/create
    // ==========================================
    @Test
    void postEvent_InvalidHours_ShouldReturnForbidden() {
        // Arrange
        EventCreateDTO dto = new EventCreateDTO();
        dto.setStartHour(LocalTime.of(20, 0));
        dto.setEndHour(LocalTime.of(18, 0));

        // Act
        ResponseEntity<?> response = eventController.postEvent(dto, authentication);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("L'esdeveniment té horaris incorrectes", response.getBody());
    }

    @Test
    void postEvent_Success_ShouldReturnCreated() throws Exception {
        // Arrange
        EventCreateDTO dto = new EventCreateDTO();
        dto.setStartHour(LocalTime.of(10, 0));
        dto.setEndHour(LocalTime.of(14, 0));
        when(eventService.createEvent(dto, userEmail)).thenReturn(dto);

        // Act
        ResponseEntity<?> response = eventController.postEvent(dto, authentication);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void postEvent_EntityNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        EventCreateDTO dto = new EventCreateDTO();
        dto.setStartHour(LocalTime.of(10, 0));
        dto.setEndHour(LocalTime.of(14, 0));
        when(eventService.createEvent(dto, userEmail)).thenThrow(new EntityNotFoundException("La etiqueta no existeix"));

        // Act
        ResponseEntity<?> response = eventController.postEvent(dto, authentication);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("La etiqueta no existeix", response.getBody());
    }

    // ==========================================
    // ENDPOINT: DELETE /event/delete/{eventId}
    // ==========================================
    @Test
    void deleteEvent_Success_ShouldReturnOk() throws Exception {
        // Act
        ResponseEntity<?> response = eventController.deleteEvent(1L, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventService, times(1)).deleteEvent(1L, userEmail);
    }

    @Test
    void deleteEvent_AccessDenied_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        doThrow(new AccessDeniedException("Sense permís!")).when(eventService).deleteEvent(1L, userEmail);

        // Act
        ResponseEntity<?> response = eventController.deleteEvent(1L, authentication);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Sense permís!", response.getBody());
    }

    // ==========================================
    // ENDPOINT: PUT /event/update
    // ==========================================
    @Test
    void updateEvent_Success_ShouldReturnOk() throws Exception {
        // Arrange
        EventUpdateDTO updateDto = new EventUpdateDTO();
        when(eventService.updateEvent(updateDto, userEmail)).thenReturn(new EventCreateDTO());

        // Act
        ResponseEntity<?> response = eventController.updateEvent(updateDto, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Esdeveniment actualitzat.", response.getBody());
    }

    // ==========================================
    // ENDPOINT: POST /event/join/{eventId}
    // ==========================================
    @Test
    void joinEvent_Success_ShouldReturnCreated() throws Exception {
        // Arrange
        AssistDTO mockAssist = new AssistDTO();
        mockAssist.setAssistId(99L);
        when(eventService.createAssist(userEmail, 1L)).thenReturn(mockAssist);

        // Act
        ResponseEntity<?> response = eventController.joinEvent(1L, authentication);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void joinEvent_AlreadyExists_ShouldReturnForbidden() throws Exception {
        // Arrange
        when(eventService.createAssist(userEmail, 1L)).thenThrow(new EntityExistsException("L'asistencia ja existeix"));

        // Act
        ResponseEntity<?> response = eventController.joinEvent(1L, authentication);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("L'asistencia ja existeix", response.getBody());
    }

    // ==========================================
    // ENDPOINT: DELETE /event/leave/{eventId}
    // ==========================================
    @Test
    void leaveEvent_Success_ShouldReturnOk() throws Exception {
        // Act
        ResponseEntity<?> response = eventController.leaveEvent(1L, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("OK!", response.getBody());
        verify(eventService, times(1)).deleteAssist(1L, userEmail);
    }
}