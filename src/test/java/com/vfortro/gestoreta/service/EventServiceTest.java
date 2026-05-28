package com.vfortro.gestoreta.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vfortro.gestoreta.conversor.AssistConversor;
import com.vfortro.gestoreta.conversor.EventConversor;
import com.vfortro.gestoreta.conversor.EventTagConversor;
import com.vfortro.gestoreta.dto.assists.AssistDTO;
import com.vfortro.gestoreta.dto.events.EventCreateDTO;
import com.vfortro.gestoreta.dto.events.EventTagAdminInfoDTO;
import com.vfortro.gestoreta.dto.events.EventUpdateDTO;
import com.vfortro.gestoreta.dto.users.UserCreateDTO;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.model.enums.UserNotificationType;
import com.vfortro.gestoreta.repository.*;
import com.vfortro.gestoreta.service.UserService;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private AssistRepository assistRepository;
    @Mock private EventTagRepository eventTagRepository;
    @Mock private FallaRepository fallaRepository;
    @Mock private AttendantRepository attendantRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserNotificationRepository notificationRepository;

    @Mock private EventConversor eventConversor;
    @Mock private AssistConversor assistConversor;
    @Mock private EventTagConversor eventTagConversor;
    @Mock private UserService userService;

    @InjectMocks
    private EventService eventService;

    private String email;
    private Falla sampleFalla;
    private Event sampleEvent;
    private EventTag sampleTag;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        email = "vicent@email.com";

        sampleFalla = new Falla();
        sampleFalla.setId(1L);

        sampleTag = new EventTag();
        sampleTag.setId(18L);
        sampleTag.setName("Menjar");

        sampleEvent = new Event();
        sampleEvent.setId(100L);
        sampleEvent.setTitle("Presentació");
        sampleEvent.setFalla(sampleFalla);
        sampleEvent.setEventTag(sampleTag);
        sampleEvent.setAttendants(new LinkedHashSet<>());
        sampleEvent.setAssists(new LinkedHashSet<>());
        sampleEvent.setPrice(10.0);

        sampleUser = new User();
        sampleUser.setId(10L);
        sampleUser.setEmail(email);
        sampleUser.setFalla(sampleFalla);
    }

    // ==========================================
    // METODO: readEvent(Long) y readEvents()
    // ==========================================
    @Test
    void readEvent_NotFound_ShouldReturnNull() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        assertNull(eventService.readEvent(1L));
    }

    @Test
    void readEvent_Found_ShouldReturnDto() {
        EventCreateDTO dto = new EventCreateDTO();
        when(eventRepository.findById(100L)).thenReturn(Optional.of(sampleEvent));
        when(eventConversor.fromEntity2Dto(sampleEvent)).thenReturn(dto);

        assertNotNull(eventService.readEvent(100L));
    }

    @Test
    void readEvents_ShouldReturnList() {
        when(eventRepository.findAll()).thenReturn(List.of(sampleEvent));
        List<Event> result = eventService.readEvents();
        assertEquals(1, result.size());
    }

    // ==========================================
    // METODO: createEvent
    // ==========================================
    @Test
    void createEvent_TagNotFound_ShouldThrowEntityNotFoundException() {
        // Arrange
        EventCreateDTO dto = new EventCreateDTO();
        dto.setTagId(99L);
        dto.setTitle("Test");

        // CORRECCIÓN: Configuramos el método correcto que usa tu código
        when(eventTagRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            eventService.createEvent(dto, email);
        });

        // Verificación opcional pero recomendada para asegurar que el mensaje es el correcto
        assertTrue(exception.getMessage().contains("La etiqueta a la que se está asociando el evento"));
    }
    @Test
    void createEvent_FallaMismatch_ShouldThrowAccessDeniedException() throws IllegalAccessException {
        EventCreateDTO dto = new EventCreateDTO();
        dto.setTagId(18L);
        dto.setFallaId(99L); // Distinta a la del usuario (1L)
        dto.setTitle("Sopar de Germanor"); // Añadido para evitar el "null" en el mensaje de error

        UserCreateDTO mockUserDto = new UserCreateDTO();
        mockUserDto.setFallaId(1L);

        // CORRECCIÓN: Simulamos "existsById" en lugar de "findById"
        when(eventTagRepository.existsById(18L)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(mockUserDto);

        assertThrows(AccessDeniedException.class, () -> eventService.createEvent(dto, email));
    }

    @Test
    void createEvent_NoManagerAccess_ShouldThrowAccessDeniedException() throws IllegalAccessException {
        EventCreateDTO dto = new EventCreateDTO();
        dto.setTagId(18L);
        dto.setFallaId(1L);

        UserCreateDTO mockUserDto = new UserCreateDTO();
        mockUserDto.setFallaId(1L);

        when(eventTagRepository.existsById(18L)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(mockUserDto);
        when(userService.checkManagerAccess(email)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> eventService.createEvent(dto, email));
    }

    @Test
    void createEvent_SuccessWithAttendants_ShouldSaveNotificationsAndAttendants() throws Exception {
        EventCreateDTO dto = new EventCreateDTO();
        dto.setTagId(18L);
        dto.setFallaId(1L);
        dto.setAttendants(List.of(10L));

        UserCreateDTO mockUserDto = new UserCreateDTO();
        mockUserDto.setFallaId(1L);

        when(eventTagRepository.existsById(18L)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(mockUserDto);
        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(eventConversor.fromDto2Entity(dto)).thenReturn(sampleEvent);
        when(eventRepository.saveAndFlush(sampleEvent)).thenReturn(sampleEvent);
        when(userService.readUserAsEntity(10L)).thenReturn(sampleUser);
        when(eventConversor.fromEntity2Dto(sampleEvent)).thenReturn(dto);

        EventCreateDTO result = eventService.createEvent(dto, email);

        assertNotNull(result);
        verify(notificationRepository, times(1)).save(any(UserNotification.class));
        verify(attendantRepository, times(1)).save(any(Attendant.class));
    }

    // ==========================================
    // METODO: deleteEvent
    // ==========================================
    @Test
    void deleteEvent_NoManagerAccess_ShouldThrowAccessDeniedException() throws IllegalAccessException {
        when(eventRepository.findById(100L)).thenReturn(Optional.of(sampleEvent));
        when(userService.checkManagerAccess(email)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> eventService.deleteEvent(100L, email));
    }

    @Test
    void deleteEvent_Success_ShouldCallDelete() throws Exception {
        UserCreateDTO mockUserDto = new UserCreateDTO();
        mockUserDto.setFallaId(1L);

        when(eventRepository.findById(100L)).thenReturn(Optional.of(sampleEvent));
        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(mockUserDto);

        eventService.deleteEvent(100L, email);
        verify(eventRepository, times(1)).delete(sampleEvent);
    }

    // ==========================================
    // METODO: updateEvent
    // ==========================================
    @Test
    void updateEvent_ChronologyError_ShouldThrowIllegalStateException() throws IllegalAccessException {
        EventUpdateDTO dto = new EventUpdateDTO();
        dto.setEventId(100L);
        dto.setStartHour(LocalTime.of(18, 0));
        dto.setEndHour(LocalTime.of(17, 0)); // Fin antes que inicio

        UserCreateDTO mockUserDto = new UserCreateDTO();
        mockUserDto.setFallaId(1L);

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(eventRepository.findById(100L)).thenReturn(Optional.of(sampleEvent));
        when(userService.readUser(email)).thenReturn(mockUserDto);

        assertThrows(IllegalStateException.class, () -> eventService.updateEvent(dto, email));
    }

    @Test
    void updateEvent_SuccessWithPriceAdjustments_ShouldKeepPaidAsTrueIfAlreadyPaid() throws Exception {
        // Arrange
        EventUpdateDTO dto = new EventUpdateDTO();
        dto.setEventId(100L);
        dto.setStartHour(LocalTime.of(10, 0));
        dto.setEndHour(LocalTime.of(12, 0));
        dto.setPrice(0.0); // Cambiamos el precio a gratis (<= 0)

        // Creamos una asistencia que YA ha pagado (true)
        Assist assist = new Assist();
        assist.setPaid(true);
        sampleEvent.getAssists().add(assist);

        UserCreateDTO mockUserDto = new UserCreateDTO();
        mockUserDto.setFallaId(1L);

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(eventRepository.findById(100L)).thenReturn(Optional.of(sampleEvent));
        when(userService.readUser(email)).thenReturn(mockUserDto);
        when(eventRepository.save(sampleEvent)).thenReturn(sampleEvent);

        // Act
        eventService.updateEvent(dto, email);

        // Assert
        // CORRECCIÓN: Ahora validamos que se mantiene a TRUE tal como deseas
        assertTrue(assist.getPaid(), "El estado 'paid' debería seguir siendo true incluso si el precio baja a 0");

        // Verificamos que se procesó la lista de asistencias en el repositorio correspondiente
        verify(assistRepository, times(1)).saveAll(anyList());
    }

    // ==========================================
    // METODO: readAssist y createAssist
    // ==========================================
    @Test
    void readAssist_Found_ShouldReturnDto() {
        Assist assist = new Assist();
        when(assistRepository.findByUserEmailAndEventId(email, 100L)).thenReturn(assist);
        when(assistConversor.formEntity2Dto(assist)).thenReturn(new AssistDTO());

        assertNotNull(eventService.readAssist(email, 100L));
    }

    @Test
    void createAssist_AlreadyExists_ShouldThrowEntityExistsException() {
        when(eventRepository.findById(100L)).thenReturn(Optional.of(sampleEvent));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));
        when(assistRepository.existsByUserIdAndEventId(10L, 100L)).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> eventService.createAssist(email, 100L));
    }

    @Test
    void createAssist_Success_ShouldSaveAndReturnDto() throws Exception {
        AssistDTO incomingDto = new AssistDTO();
        Assist entity = new Assist();

        when(eventRepository.findById(100L)).thenReturn(Optional.of(sampleEvent));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));
        when(assistRepository.existsByUserIdAndEventId(10L, 100L)).thenReturn(false);
        when(assistConversor.fromDto2Entity(any(AssistDTO.class))).thenReturn(entity);
        when(assistRepository.saveAndFlush(entity)).thenReturn(entity);
        when(assistConversor.formEntity2Dto(entity)).thenReturn(new AssistDTO());

        assertNotNull(eventService.createAssist(email, 100L));
    }

    // ==========================================
    // METODO: deleteAssist
    // ==========================================
    @Test
    void deleteAssist_OwnershipMismatch_ShouldThrowIllegalStateException() {
        Assist assist = new Assist();
        User wrongUser = new User();
        wrongUser.setId(99L); // Id distinto al del solicitante
        assist.setUser(wrongUser);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser)); // sampleUser tiene Id = 10L
        when(assistRepository.findById(1L)).thenReturn(Optional.of(assist));

        assertThrows(IllegalStateException.class, () -> eventService.deleteAssist(1L, email));
    }

    @Test
    void deleteAssist_Success_ShouldDelete() {
        Assist assist = new Assist();
        assist.setUser(sampleUser);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));
        when(assistRepository.findById(1L)).thenReturn(Optional.of(assist));

        eventService.deleteAssist(1L, email);
        verify(assistRepository, times(1)).delete(assist);
    }

    // ==========================================
    // METODO: payAssist
    // ==========================================
    @Test
    void payAssist_NotPayableEvent_ShouldThrowIllegalStateException() {
        Assist assist = new Assist();
        assist.setPaid(null); // Evento que no requiere pago

        when(assistRepository.findByUserIdAndEventId(10L, 100L)).thenReturn(Optional.of(assist));

        assertThrows(IllegalStateException.class, () -> eventService.payAssist(10L, 100L));
    }

    @Test
    void payAssist_AlreadyPaid_ShouldThrowIllegalStateException() {
        Assist assist = new Assist();
        assist.setPaid(true); // Ya pagado

        when(assistRepository.findByUserIdAndEventId(10L, 100L)).thenReturn(Optional.of(assist));

        assertThrows(IllegalStateException.class, () -> eventService.payAssist(10L, 100L));
    }

    @Test
    void payAssist_Success_ShouldSetTrueAndSave() {
        Assist assist = new Assist();
        assist.setPaid(false); // Pendiente de pago

        when(assistRepository.findByUserIdAndEventId(10L, 100L)).thenReturn(Optional.of(assist));

        eventService.payAssist(10L, 100L);

        assertTrue(assist.getPaid());
        verify(assistRepository, times(1)).saveAndFlush(assist);
    }

    // ==========================================
    // METODO: getTotalRevenue
    // ==========================================
    @Test
    void getTotalRevenue_WithValidEvents_ShouldCalculateRevenueAndSaveAll() {
        Assist paidAssist1 = new Assist();
        paidAssist1.setPaid(true);
        Assist unpaidAssist = new Assist();
        unpaidAssist.setPaid(false);

        sampleEvent.getAssists().add(paidAssist1);
        sampleEvent.getAssists().add(unpaidAssist);
        sampleEvent.setPrice(25.0); // 1 asistencia pagada * 25 = 25.0 revenue

        when(eventRepository.findByTotalRevenueIsNotNullAndInactiveAndWithPrice()).thenReturn(List.of(sampleEvent));

        eventService.getTotalRevenue();

        assertEquals(25.0, sampleEvent.getTotalRevenue());
        verify(eventRepository, times(1)).saveAll(anyList());
    }

    @Test
    void updateEvent_WithNewAttendants_ShouldClearOldAndCreateNotifications() throws Exception {
        // Arrange
        Long eventId = 100L;
        EventUpdateDTO updateDto = new EventUpdateDTO();
        updateDto.setEventId(eventId);
        updateDto.setStartHour(LocalTime.of(10, 0));
        updateDto.setEndHour(LocalTime.of(14, 0));
        updateDto.setAttendantIds(List.of(1L)); // Añadimos ID del nuevo encargado

        Falla mockFalla = new Falla();
        mockFalla.setId(5L);

        UserCreateDTO managerDto = new UserCreateDTO();
        managerDto.setFallaId(5L);

        EventTag mockTag = new EventTag();
        mockTag.setName("Cultura");

        Event existingEvent = new Event();
        existingEvent.setFalla(mockFalla);
        existingEvent.setTitle("Presentació de l'esdeveniment");
        existingEvent.setEventTag(mockTag);
        existingEvent.setAttendants(new LinkedHashSet<>()); // Lista mutable controlada para el test
        existingEvent.setAssists(new LinkedHashSet<>());

        User mockAttendantUser = new User();
        mockAttendantUser.setId(1L);

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        when(userService.readUser(email)).thenReturn(managerDto);

        // Mockeos específicos de la rama Attendants
        when(userService.readUserAsEntity(1L)).thenReturn(mockAttendantUser);
        when(attendantRepository.existsByUser_IdAndEvent_Id(1L, eventId)).thenReturn(false); // No existe previo

        when(eventRepository.save(existingEvent)).thenReturn(existingEvent);

        EventCreateDTO expectedResponse = new EventCreateDTO();
        lenient().when((eventConversor).fromEntity2Dto(existingEvent)).thenReturn(expectedResponse);

        // Act
        EventCreateDTO result = eventService.updateEvent(updateDto, email);

        // Assert
        assertNotNull(result);
        assertEquals(1, existingEvent.getAttendants().size());
        assertEquals(mockAttendantUser, existingEvent.getAttendants().stream().findFirst().get().getUser());

        // Verificamos que se construyó y persistió la notificación correctamente
        verify(notificationRepository, times(1)).save(argThat(notification ->
                notification.getUser() == mockAttendantUser &&
                        notification.getType() == UserNotificationType.EVENT_ATTENDANT_PETITION &&
                        !notification.getRead() &&
                        notification.getMessage().contains("Presentació de l'esdeveniment") &&
                        notification.getMessage().contains("Cultura")
        ));
    }

    // =========================================================================
    // RAMA: attendantRepository.existsByUser_IdAndEvent_Id() -> Salta con un break
    // =========================================================================
    @Test
    void updateEvent_WithExistingAttendant_ShouldTriggerBreak() throws Exception {
        // Arrange
        Long eventId = 100L;
        EventUpdateDTO updateDto = new EventUpdateDTO();
        updateDto.setEventId(eventId);
        updateDto.setStartHour(LocalTime.of(10, 0));
        updateDto.setEndHour(LocalTime.of(14, 0));
        updateDto.setAttendantIds(List.of(2L, 3L)); // Pasamos dos IDs para validar que frena la iteración

        Falla mockFalla = new Falla();
        mockFalla.setId(5L);

        UserCreateDTO managerDto = new UserCreateDTO();
        managerDto.setFallaId(5L);

        Event existingEvent = new Event();
        existingEvent.setFalla(mockFalla);
        existingEvent.setAttendants(new LinkedHashSet<>());
        existingEvent.setAssists(new LinkedHashSet<>());

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        when(userService.readUser(email)).thenReturn(managerDto);

        // Mockeamos que el primer ID (2L) ya existe en base de datos vinculada a este evento
        when(attendantRepository.existsByUser_IdAndEvent_Id(2L, eventId)).thenReturn(true);

        when(eventRepository.save(existingEvent)).thenReturn(existingEvent);

        // Act
        eventService.updateEvent(updateDto, email);

        // Assert
        // El bucle hace un break inmediato en el id 2L. No debe procesar el 3L ni persistir nada.
        verify(userService, never()).readUserAsEntity(3L);
        verifyNoInteractions(notificationRepository);
        assertTrue(existingEvent.getAttendants().isEmpty());
    }
}