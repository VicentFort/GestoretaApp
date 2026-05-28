package com.vfortro.gestoreta.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vfortro.gestoreta.conversor.FallaConversor;
import com.vfortro.gestoreta.dto.fallas.FallaCreateDTO;
import com.vfortro.gestoreta.dto.fallas.FallaUpdateDTO;
import com.vfortro.gestoreta.dto.fallas.info.FallaAdminInfoDTO;
import com.vfortro.gestoreta.dto.fallas.info.FallaUserInfoDTO;
import com.vfortro.gestoreta.dto.users.UserCreateDTO;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.model.enums.AccessType;
import com.vfortro.gestoreta.repository.ChargeRepository;
import com.vfortro.gestoreta.repository.EventTagRepository;
import com.vfortro.gestoreta.repository.FallaRepository;
import com.vfortro.gestoreta.repository.UserRepository;
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
import java.util.LinkedHashSet;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class FallaServiceTest {

    @Mock private FallaRepository fallaRepository;
    @Mock private EventTagRepository eventTagRepository;
    @Mock private UserRepository userRepository;
    @Mock private ChargeRepository chargeRepository;
    @Mock private FallaConversor fallaConversor;
    @Mock private UserService userService;

    @InjectMocks
    private FallaService fallaService;

    private final String emailGestor = "manager@falla.com";
    private Falla sampleFalla;
    private UserCreateDTO sampleUserDto;
    private User sampleManager;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleFalla = new Falla();
        sampleFalla.setId(1L);
        sampleFalla.setName("Falla Torrent");
        sampleFalla.setCreationDate(LocalDate.of(2000, 1, 1));

        sampleUserDto = new UserCreateDTO();
        sampleUserDto.setFallaId(1L);

        sampleManager = new User();
        sampleManager.setId(10L);
        sampleManager.setEmail(emailGestor);
        sampleManager.setFalla(sampleFalla);

        sampleUser = new User();
        sampleUser.setId(20L);
        sampleUser.setFalla(sampleFalla);
        sampleUser.setCharges(new LinkedHashSet<>()); // Inicializado como LinkedHashSet
    }

    // ==========================================
    // MÉTODO: createFalla
    // ==========================================
    @Test
    void createFalla_ShouldReturnSavedDto() {
        FallaCreateDTO dto = new FallaCreateDTO();
        when(fallaConversor.fromDto2Entity(dto)).thenReturn(sampleFalla);
        when(fallaRepository.saveAndFlush(sampleFalla)).thenReturn(sampleFalla);
        when(fallaConversor.fromEntity2DTO(sampleFalla)).thenReturn(dto);

        assertNotNull(fallaService.createFalla(dto));
    }

    // ==========================================
    // MÉTODO: updateFalla
    // ==========================================
    @Test
    void updateFalla_NoManagerAccess_ShouldThrowAccessDeniedException() throws IllegalAccessException {
        when(userService.checkManagerAccess(emailGestor)).thenReturn(false);
        assertThrows(AccessDeniedException.class, () -> fallaService.updateFalla(new FallaUpdateDTO(), emailGestor));
    }

    @Test
    void updateFalla_UserHasNoFallaId_ShouldThrowNullPointerException() throws IllegalAccessException {
        sampleUserDto.setFallaId(null); // Provocamos el NullPointerException
        when(userService.checkManagerAccess(emailGestor)).thenReturn(true);
        when(userService.readUser(emailGestor)).thenReturn(sampleUserDto);

        assertThrows(NullPointerException.class, () -> fallaService.updateFalla(new FallaUpdateDTO(), emailGestor));
    }

    @Test
    void updateFalla_Success_ShouldModifyFieldsAndSave() throws Exception {
        FallaUpdateDTO updateDTO = new FallaUpdateDTO();
        updateDTO.setName("Nou Nom Falla");

        when(userService.checkManagerAccess(emailGestor)).thenReturn(true);
        when(userService.readUser(emailGestor)).thenReturn(sampleUserDto);
        when(fallaRepository.findFallaById(1L)).thenReturn(sampleFalla);

        fallaService.updateFalla(updateDTO, emailGestor);

        assertEquals("Nou Nom Falla", sampleFalla.getName());
        verify(fallaRepository, times(1)).saveAndFlush(sampleFalla);
    }

    // ==========================================
    // MÉTODO: readFalla
    // ==========================================
    @Test
    void readFalla_NotFound_ShouldReturnNull() {
        when(fallaRepository.findById(1L)).thenReturn(Optional.empty());
        assertNull(fallaService.readFalla(1L));
    }

    @Test
    void readFalla_Found_ShouldReturnUserInfoDTO() {
        when(fallaRepository.findById(1L)).thenReturn(Optional.of(sampleFalla));

        FallaUserInfoDTO result = fallaService.readFalla(1L);

        assertNotNull(result);
        assertEquals(1L, result.getFallaId());
        assertEquals("Falla Torrent", result.getName());
    }

    // ==========================================
    // METODO: addEventTag
    // ==========================================
    @Test
    void addEventTag_NoFallaAssigned_ShouldThrowEntityNotFoundException() {
        sampleUserDto.setFallaId(null);
        when(userService.readUser(emailGestor)).thenReturn(sampleUserDto);

        assertThrows(EntityNotFoundException.class, () -> fallaService.addEventTag(emailGestor, "Infantil"));
    }

    @Test
    void addEventTag_TagAlreadyExists_ShouldThrowEntityExistsException() throws IllegalAccessException {
        when(userService.readUser(emailGestor)).thenReturn(sampleUserDto);
        when(userService.checkManagerAccess(emailGestor)).thenReturn(true);
        when(fallaRepository.findFallaById(1L)).thenReturn(sampleFalla);
        // Simulamos que el validador encuentra que ya existe el nombre
        when(eventTagRepository.existsEventTagByNameAndFalla("Cultural", sampleFalla)).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> fallaService.addEventTag(emailGestor, "Cultural"));
    }

    @Test
    void addEventTag_Success_ShouldSaveNewTag() throws Exception {
        when(userService.readUser(emailGestor)).thenReturn(sampleUserDto);
        when(userService.checkManagerAccess(emailGestor)).thenReturn(true);
        when(fallaRepository.findFallaById(1L)).thenReturn(sampleFalla);
        when(eventTagRepository.existsEventTagByNameAndFalla("Cultural", sampleFalla)).thenReturn(false);

        fallaService.addEventTag(emailGestor, "Cultural");

        verify(eventTagRepository, times(1)).saveAndFlush(any(EventTag.class));
    }

    // ==========================================
    // MÉTODO: getFallaInfo
    // ==========================================
    @Test
    void getFallaInfo_Success_ShouldReturnAdminDTO() throws Exception {
        when(userService.readUser(emailGestor)).thenReturn(sampleUserDto);
        when(userService.checkManagerAccess(emailGestor)).thenReturn(true);
        when(fallaRepository.findFallaById(1L)).thenReturn(sampleFalla);
        when(fallaConversor.fromEntity2AdminInfo(sampleFalla)).thenReturn(new FallaAdminInfoDTO());

        assertNotNull(fallaService.getFallaInfo(emailGestor));
    }

    // ==========================================
    // MÉTODO: editAccessType
    // ==========================================
    @Test
    void editAccessType_UserHasNoFalla_ShouldThrowIllegalAccessException() throws IllegalAccessException {
        sampleUser.setFalla(null); // Provocamos que el usuario no esté asignado a ninguna falla

        when(userRepository.findById(20L)).thenReturn(Optional.of(sampleUser));
        when(userService.checkSuperUserAccess(emailGestor)).thenReturn(true);
        when(userRepository.findByEmail(emailGestor)).thenReturn(Optional.of(sampleManager));

        assertThrows(IllegalAccessException.class, () ->
                fallaService.editAccessType(20L, AccessType.MANAGER, emailGestor)
        );
    }

    @Test
    void editAccessType_FallaRepositoryCheckFails_ShouldThrowEntityNotFoundException() throws IllegalAccessException {
        when(userRepository.findById(20L)).thenReturn(Optional.of(sampleUser));
        when(userService.checkSuperUserAccess(emailGestor)).thenReturn(true);
        when(userRepository.findByEmail(emailGestor)).thenReturn(Optional.of(sampleManager));
        // La falla asignada en BD no existe según el repositorio
        when(fallaRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                fallaService.editAccessType(20L, AccessType.MANAGER, emailGestor)
        );
    }

    @Test
    void editAccessType_FallaMismatch_ShouldThrowIllegalStateException() throws IllegalAccessException {
        Falla otraFalla = new Falla();
        otraFalla.setId(99L);
        sampleUser.setFalla(otraFalla); // Falla del usuario diferente a la del Gestor

        when(userRepository.findById(20L)).thenReturn(Optional.of(sampleUser));
        when(userService.checkSuperUserAccess(emailGestor)).thenReturn(true);
        when(userRepository.findByEmail(emailGestor)).thenReturn(Optional.of(sampleManager));
        when(fallaRepository.existsById(99L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                fallaService.editAccessType(20L, AccessType.MANAGER, emailGestor)
        );
    }

    @Test
    void editAccessType_SuccessWithExistingCharge_ShouldModifyAndSave() throws Exception {
        Charge existingCharge = new Charge();
        existingCharge.setType(AccessType.EMPTY_CHARGE);
        sampleUser.getCharges().add(existingCharge);

        when(userRepository.findById(20L)).thenReturn(Optional.of(sampleUser));
        when(userService.checkSuperUserAccess(emailGestor)).thenReturn(true);
        when(userRepository.findByEmail(emailGestor)).thenReturn(Optional.of(sampleManager));
        when(fallaRepository.existsById(1L)).thenReturn(true);

        fallaService.editAccessType(20L, AccessType.SUPERUSER, emailGestor);

        assertEquals(AccessType.SUPERUSER, existingCharge.getType());
        verify(chargeRepository, times(1)).save(existingCharge);
    }

    @Test
    void editAccessType_SuccessWithNoPriorCharge_ShouldCreateAndSave() throws Exception {
        // En este escenario sampleUser.getCharges() está vacío
        when(userRepository.findById(20L)).thenReturn(Optional.of(sampleUser));
        when(userService.checkSuperUserAccess(emailGestor)).thenReturn(true);
        when(userRepository.findByEmail(emailGestor)).thenReturn(Optional.of(sampleManager));
        when(fallaRepository.existsById(1L)).thenReturn(true);

        fallaService.editAccessType(20L, AccessType.MANAGER, emailGestor);

        verify(chargeRepository, times(1)).save(argThat(charge ->
                charge.getType() == AccessType.MANAGER &&
                        charge.getUser() == sampleUser &&
                        charge.getFalla() == sampleFalla
        ));
    }

    // ==========================================
    // MÉTODO: deleteEventTag
    // ==========================================
    @Test
    void deleteEventTag_TagNotFound_ShouldThrowEntityNotFoundException() throws IllegalAccessException {
        when(userService.checkManagerAccess(emailGestor)).thenReturn(true);
        when(eventTagRepository.existsById(40L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> fallaService.deleteEventTag(40L, emailGestor));
    }

    @Test
    void deleteEventTag_Success_ShouldCallDelete() throws Exception {
        when(userService.checkManagerAccess(emailGestor)).thenReturn(true);
        when(eventTagRepository.existsById(40L)).thenReturn(true);

        fallaService.deleteEventTag(40L, emailGestor);

        verify(eventTagRepository, times(1)).deleteById(40L);
    }
}