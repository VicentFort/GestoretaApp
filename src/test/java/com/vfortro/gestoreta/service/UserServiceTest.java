package com.vfortro.gestoreta.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vfortro.gestoreta.conversor.RequestConversor;
import com.vfortro.gestoreta.conversor.UserConversor;
import com.vfortro.gestoreta.dto.requests.RequestCreateDTO;
import com.vfortro.gestoreta.dto.requests.RequestUpdateDTO;
import com.vfortro.gestoreta.dto.users.UserCreateDTO;
import com.vfortro.gestoreta.dto.users.UserUpdateDTO;
import com.vfortro.gestoreta.dto.users.info.UserInfoDTO;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.model.enums.AccessType;
import com.vfortro.gestoreta.model.enums.FoodNeedType;
import com.vfortro.gestoreta.repository.*;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private EventTagRepository eventTagRepository;
    @Mock private AttendandPreferenceRepository attendandPreferenceRepository;
    @Mock private RequestRepository requestRepository;
    @Mock private ChargeRepository chargeRepository;
    @Mock private FallaRepository fallaRepository;
    @Mock private UserNotificationRepository notificationRepository;
    @Mock private UserConversor userConversor;
    @Mock private RequestConversor requestConversor;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private String email;
    private User sampleUser;
    private Falla sampleFalla;

    @BeforeEach
    void setUp() {
        email = "vicent@email.com";
        sampleFalla = new Falla();
        sampleFalla.setId(1L);

        sampleUser = new User();
        sampleUser.setId(10L);
        sampleUser.setEmail(email);
        sampleUser.setFalla(sampleFalla);
        sampleUser.setNeeds(new ArrayList<>());
        sampleUser.setNotifications(new LinkedHashSet<>());
        sampleUser.setCharges(new LinkedHashSet<>());
    }

    // ==========================================
    // METODO: createUser
    // ==========================================
    @Test
    void createUser_WhenEmailExists_ShouldThrowEntityExistsException() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setEmail(email);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> userService.createUser(dto));
    }

    @Test
    void createUser_WhenUserIsUnderage_ShouldThrowIllegalArgumentException() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setEmail(email);
        dto.setBirthday(LocalDate.now().minusYears(17)); // Menor de edad
        when(userRepository.existsByEmail(email)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
    }

    @Test
    void createUser_WhenSuccessfulWithAccessType_ShouldSaveUserAndCharge() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setEmail(email);
        dto.setBirthday(LocalDate.now().minusYears(20));
        dto.setPassword("rawPassword");
        dto.setAccessType(AccessType.MANAGER);

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userConversor.fromDto2Entity(dto)).thenReturn(sampleUser);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(sampleUser);
        when(userConversor.fromEntity2Dto(sampleUser)).thenReturn(dto);

        UserCreateDTO result = userService.createUser(dto);

        assertNotNull(result);
        verify(chargeRepository, times(1)).saveAndFlush(any(Charge.class));
    }

    // ==========================================
    // METODO: readUser(String email)
    // ==========================================
    @Test
    void readUserByEmail_WhenUserExists_ShouldReturnDto() {
        UserCreateDTO dto = new UserCreateDTO();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));
        when(userConversor.fromEntity2Dto(sampleUser)).thenReturn(dto);

        assertNotNull(userService.readUser(email));
    }

    @Test
    void readUserByEmail_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.readUser(email));
    }

    // ==========================================
    // METODO: readUser(Long userId)
    // ==========================================
    @Test
    void readUserById_WhenUserExists_ShouldReturnDto() {
        UserCreateDTO dto = new UserCreateDTO();
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(userConversor.fromEntity2Dto(sampleUser)).thenReturn(dto);

        assertNotNull(userService.readUser(10L));
    }

    @Test
    void readUserById_WhenUserDoesNotExist_ShouldReturnNull() {
        when(userRepository.findById(10L)).thenReturn(Optional.empty());
        assertNull(userService.readUser(10L));
    }

    // ==========================================
    // METODO: updateUser
    // ==========================================
    @Test
    void updateUser_WhenUserDoesNotExist_ShouldThrowNullPointerException() {
        when(userRepository.existsByEmail(email)).thenReturn(false);
        assertThrows(NullPointerException.class, () -> userService.updateUser(new UserUpdateDTO(), email));
    }

    @Test
    void updateUser_WhenEmailMismatch_ShouldThrowAccessDeniedException() {
        when(userRepository.existsByEmail(email)).thenReturn(true);
        User wrongUser = new User();
        wrongUser.setEmail("wrong@email.com");
        when(userRepository.findUserByEmail(email)).thenReturn(wrongUser);

        assertThrows(AccessDeniedException.class, () -> userService.updateUser(new UserUpdateDTO(), email));
    }

    @Test
    void updateUser_WhenSuccessful_ShouldUpdateFields() throws AccessDeniedException {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setName("NewName");
        updateDTO.setSurname("NewSurname");

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.findUserByEmail(email)).thenReturn(sampleUser);
        when(userRepository.saveAndFlush(sampleUser)).thenReturn(sampleUser);
        when(userConversor.fromEntity2InfoDto(sampleUser)).thenReturn(new UserInfoDTO());

        UserInfoDTO result = userService.updateUser(updateDTO, email);
        assertNotNull(result);
        assertEquals("NewName", sampleUser.getName());
        assertEquals("NewSurname", sampleUser.getSurname());
    }

    // ==========================================
    // METODO: readUserSecure
    // ==========================================
    @Test
    void readUserSecure_WhenEmailsMatch_ShouldReturnInfoDto() throws AccessDeniedException {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUserId(10L); // Mismo ID que sampleUser

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));
        when(userConversor.fromEntity2Dto(sampleUser)).thenReturn(createDTO);
        when(userConversor.fromEntity2InfoDto(sampleUser)).thenReturn(new UserInfoDTO());

        assertNotNull(userService.readUserSecure(email));
    }

    @Test
    void readUserSecure_WhenIdMismatch_ShouldThrowAccessDeniedException() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUserId(99L); // ID Diferente

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));
        when(userConversor.fromEntity2Dto(sampleUser)).thenReturn(createDTO);

        assertThrows(AccessDeniedException.class, () -> userService.readUserSecure(email));
    }

    // ==========================================
    // METODO: readUserAsEntity(Long) y (String)
    // ==========================================
    @Test
    void readUserAsEntityById_NotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.readUserAsEntity(1L));
    }

    @Test
    void readUserAsEntityByEmail_NotFound_ShouldThrowException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.readUserAsEntity(email));
    }

    // ==========================================
    // METODO: createAttPreferences
    // ==========================================
    @Test
    void createAttPreferences_FallaMismatch_ShouldThrowAccessDeniedException() {
        EventTag tag = new EventTag();
        Falla otraFalla = new Falla();
        otraFalla.setId(99L);
        tag.setFalla(otraFalla);

        when(userRepository.findUserByEmail(email)).thenReturn(sampleUser);
        when(eventTagRepository.findTagById(1L)).thenReturn(tag);

        assertThrows(AccessDeniedException.class, () -> userService.createAttPreferences(email, 1L));
    }

    @Test
    void createAttPreferences_PreferenceAlreadyExists_ShouldThrowEntityExistsException() {
        EventTag tag = new EventTag();
        tag.setId(1L);
        tag.setFalla(sampleFalla);

        AttendantPreference existingPref = new AttendantPreference();
        existingPref.setUser(sampleUser);
        existingPref.setEventTag(tag);
        tag.setAttendantPreferences(Set.of(existingPref));

        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUserId(10L);

        when(userRepository.findUserByEmail(email)).thenReturn(sampleUser);
        when(eventTagRepository.findTagById(1L)).thenReturn(tag);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));
        when(userConversor.fromEntity2Dto(sampleUser)).thenReturn(createDTO);

        assertThrows(EntityExistsException.class, () -> userService.createAttPreferences(email, 1L));
    }

    @Test
    void createAttPreferences_Success_ShouldSavePreference() throws AccessDeniedException {
        EventTag tag = new EventTag();
        tag.setId(1L);
        tag.setFalla(sampleFalla);
        tag.setAttendantPreferences(new LinkedHashSet<>());

        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUserId(10L);

        when(userRepository.findUserByEmail(email)).thenReturn(sampleUser);
        when(eventTagRepository.findTagById(1L)).thenReturn(tag);

        userService.createAttPreferences(email, 1L);
        verify(attendandPreferenceRepository, times(1)).saveAndFlush(any(AttendantPreference.class));
    }

    // ==========================================
    // METODO: removeAttPrefs
    // ==========================================
    @Test
    void removeAttPrefs_FallaMismatch_ShouldThrowAccessDeniedException() {
        User userInPref = new User();
        Falla otraFalla = new Falla();
        otraFalla.setId(99L);
        userInPref.setFalla(otraFalla);

        AttendantPreference pref = new AttendantPreference();
        pref.setUser(userInPref);

        when(userRepository.findUserByEmail(email)).thenReturn(sampleUser);
        when(attendandPreferenceRepository.getAllByUser(sampleUser)).thenReturn(List.of(pref));

        assertThrows(AccessDeniedException.class, () -> userService.removeAttPrefs(email, List.of(1L)));
    }

    @Test
    void removeAttPrefs_Success_ShouldDelete() throws AccessDeniedException {
        AttendantPreference pref = new AttendantPreference();
        pref.setUser(sampleUser);

        when(userRepository.findUserByEmail(email)).thenReturn(sampleUser);
        when(attendandPreferenceRepository.getAllByUser(sampleUser)).thenReturn(List.of(pref));

        userService.removeAttPrefs(email, List.of(1L));
        verify(attendandPreferenceRepository, times(1)).deleteAllById(List.of(1L));
    }

    // ==========================================
    // METODOS: FoodNeed (create y delete)
    // ==========================================
    @Test
    void createFoodNeed_AlreadyExists_ShouldThrowIllegalStateException() {
        sampleUser.getNeeds().add(FoodNeedType.VEGAN);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));

        assertThrows(IllegalStateException.class, () -> userService.createFoodNeed("Vegà", email));
    }

    @Test
    void createFoodNeed_Success_ShouldAddAndSave() throws AccessDeniedException {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));

        userService.createFoodNeed("Vegà", email);
        assertTrue(sampleUser.getNeeds().contains(FoodNeedType.VEGAN));
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    void deleteFoodNeed_NotPresent_ShouldThrowAccessDeniedException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));
        assertThrows(AccessDeniedException.class, () -> userService.deleteFoodNeed("VEGAN", email));
    }

    // ==========================================
    // METODO: readRequest
    // ==========================================
    @Test
    void readRequest_NotFound_ShouldReturnNull() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());
        assertNull(userService.readRequest(1L));
    }

    // ==========================================
    // METODO: createRequest
    // ==========================================
    @Test
    void createRequest_IdMismatch_ShouldThrowAccessDeniedException() {
        RequestCreateDTO dto = new RequestCreateDTO();
        dto.setIdUser(99L); // Diferente al del usuario (10L)

        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUserId(10L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));
        when(userConversor.fromEntity2Dto(sampleUser)).thenReturn(createDTO);

        assertThrows(AccessDeniedException.class, () -> userService.createRequest(dto, email));
    }

    @Test
    void createRequest_UserAlreadyHasFalla_ShouldThrowIllegalAccessException() {
        RequestCreateDTO dto = new RequestCreateDTO();
        dto.setIdUser(10L);

        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUserId(10L);

        Request requestEntity = new Request();
        requestEntity.setUser(sampleUser); // sampleUser tiene falla asignada en el setUp

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));
        when(userConversor.fromEntity2Dto(sampleUser)).thenReturn(createDTO);
        when(requestConversor.fromDto2Entity(dto)).thenReturn(requestEntity);

        assertThrows(IllegalAccessException.class, () -> userService.createRequest(dto, email));
    }

    // ==========================================
    // METODO: updateRequest
    // ==========================================
    @Test
    void updateRequest_FallaIdMismatch_ShouldThrowAccessDeniedException() {
        RequestUpdateDTO dto = new RequestUpdateDTO();
        dto.setIdFalla(99L); // Distinta a sampleFalla (1L)

        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setFallaId(1L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));
        when(userConversor.fromEntity2Dto(sampleUser)).thenReturn(createDTO);

        assertThrows(AccessDeniedException.class, () -> userService.updateRequest(dto, email));
    }

    // ==========================================
    // METODOS: checkManagerAccess y checkSuperUserAccess
    // ==========================================
    @Test
    void checkManagerAccess_NoFalla_ShouldThrowIllegalAccessException() {
        sampleUser.setFalla(null);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));

        assertThrows(IllegalAccessException.class, () -> userService.checkManagerAccess(email));
    }

    @Test
    void checkManagerAccess_HasManagerCharge_ShouldReturnTrue() throws IllegalAccessException {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));
        when(fallaRepository.existsById(sampleFalla.getId())).thenReturn(true);

        Charge managerCharge = new Charge();
        managerCharge.setType(AccessType.MANAGER);
        sampleUser.getCharges().add(managerCharge);

        assertTrue(userService.checkManagerAccess(email));
    }

    // ==========================================
    // METODOS: changePfp y downloadPfp
    // ==========================================
    @Test
    void changePfp_Success_ShouldSaveBytes() throws IOException {
        MultipartFile mockFile = new MockMultipartFile("pfp", "test.png", "image/png", "bytes".getBytes());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));

        userService.changePfp(mockFile, email);

        assertNotNull(sampleUser.getPfpContent());
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    void downloadPfp_NullContent_ShouldThrowNullPointerException() {
        sampleUser.setPfpContent(null);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));

        assertThrows(NullPointerException.class, () -> userService.downloadPfp(email));
    }

    @Test
    void downloadPfp_Success_ShouldReturnResource() {
        sampleUser.setPfpContent("bytes".getBytes());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));

        ByteArrayResource resource = userService.downloadPfp(email);
        assertNotNull(resource);
        assertEquals("bytes", new String(resource.getByteArray()));
    }

    // ==========================================
    // METODO: readNotification
    // ==========================================
    @Test
    void readNotification_NotBelongingToUser_ShouldThrowAccessDeniedException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));
        // El usuario no tiene la notificación con ID 1L en su lista

        assertThrows(AccessDeniedException.class, () -> userService.readNotification(1L, email));
    }

    @Test
    void readNotification_AlreadyRead_ShouldThrowIllegalStateException() {
        UserNotification notification = new UserNotification();
        notification.setNotificationId(1L);
        notification.setRead(true); // Ya leída
        sampleUser.getNotifications().add(notification);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        assertThrows(IllegalStateException.class, () -> userService.readNotification(1L, email));
    }

    @Test
    void readNotification_Success_ShouldMarkAsReadAndSave() throws AccessDeniedException {
        UserNotification notification = new UserNotification();
        notification.setNotificationId(1L);
        notification.setRead(false);
        sampleUser.getNotifications().add(notification);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        userService.readNotification(1L, email);

        assertTrue(notification.getRead());
        verify(notificationRepository, times(1)).save(notification);
    }

    // =========================================================================
    // CASO 1: El usuario no existe en la Base de Datos
    // =========================================================================
    @Test
    void checkSuperUserAccess_UserNotFound_ShouldThrowEntityNotFoundException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.checkSuperUserAccess(email)
        );

        verifyNoInteractions(fallaRepository);
    }

    // =========================================================================
    // CASO 2: El usuario existe pero no tiene ninguna Falla asociada (falla == null)
    // =========================================================================
    @Test
    void checkSuperUserAccess_UserHasNoFalla_ShouldThrowIllegalAccessException() {
        User mockUser = new User();
        mockUser.setFalla(null); // Explicitamente sin falla

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        assertThrows(IllegalAccessException.class, () ->
                userService.checkSuperUserAccess(email)
        );

        verifyNoInteractions(fallaRepository);
    }

    // =========================================================================
    // CASO 3: El usuario tiene Falla, pero el ID de esa Falla no existe en la BD
    // =========================================================================
    @Test
    void checkSuperUserAccess_FallaDoesNotExistInDb_ShouldThrowEntityExistsException() {
        Falla mockFalla = new Falla();
        mockFalla.setId(99L);

        User mockUser = new User();
        mockUser.setFalla(mockFalla);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(fallaRepository.existsById(99L)).thenReturn(false); // No existe en BD

        assertThrows(EntityExistsException.class, () ->
                userService.checkSuperUserAccess(email)
        );
    }

    // =========================================================================
    // CASO 4: Éxito - El usuario cumple requisitos pero NO tiene el rol SUPERUSER (Retorna false)
    // =========================================================================
    @Test
    void checkSuperUserAccess_NoSuperUserCharge_ShouldReturnFalse() throws Exception {
        Falla mockFalla = new Falla();
        mockFalla.setId(1L);

        Charge standardCharge = new Charge();
        standardCharge.setType(AccessType.EMPTY_CHARGE); // Rol normal, no es SUPERUSER

        User mockUser = new User();
        mockUser.setFalla(mockFalla);
        mockUser.setCharges(Set.of(standardCharge));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(fallaRepository.existsById(1L)).thenReturn(true);

        Boolean result = userService.checkSuperUserAccess(email);

        assertFalse(result);
    }

    // =========================================================================
    // CASO 5: Éxito - El usuario tiene cargos vacíos o nulos (Retorna false)
    // =========================================================================
    @Test
    void checkSuperUserAccess_ChargesEmptyOrNull_ShouldReturnFalse() throws Exception {
        Falla mockFalla = new Falla();
        mockFalla.setId(1L);

        User mockUser = new User();
        mockUser.setFalla(mockFalla);
        mockUser.setCharges(new LinkedHashSet<>()); // Lista vacía

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(fallaRepository.existsById(1L)).thenReturn(true);

        Boolean result = userService.checkSuperUserAccess(email);

        assertFalse(result);
    }

    // =========================================================================
    // CASO 6: Éxito - El usuario tiene el rol SUPERUSER (Retorna true)
    // =========================================================================
    @Test
    void checkSuperUserAccess_HasSuperUserCharge_ShouldReturnTrue() throws Exception {
        Falla mockFalla = new Falla();
        mockFalla.setId(1L);

        Charge superUserCharge = new Charge();
        superUserCharge.setType(AccessType.SUPERUSER); // Rol requerido

        User mockUser = new User();
        mockUser.setFalla(mockFalla);
        mockUser.setCharges(Set.of(superUserCharge));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(fallaRepository.existsById(1L)).thenReturn(true);

        Boolean result = userService.checkSuperUserAccess(email);

        assertTrue(result);
    }



    // =========================================================================
    // TESTS PARA: updateRequest (Corregidos con Spy para evitar NPE)
    // =========================================================================

    // RAMA: El usuario comparte idFalla, pero NO tiene acceso de mánager (checkManagerAccess == false)
    @Test
    void updateRequest_NoManagerAccess_ShouldThrowAccessDeniedException() throws Exception {
        // 1. Convertimos la instancia en un Spy para poder mockear sus propios métodos internos
        UserService userServiceSpy = spy(userService);

        RequestUpdateDTO dto = new RequestUpdateDTO();
        dto.setIdFalla(1L);
        dto.setRequestId(10L);

        com.vfortro.gestoreta.dto.users.UserCreateDTO mockUserDto = new com.vfortro.gestoreta.dto.users.UserCreateDTO();
        mockUserDto.setFallaId(1L); // Coincide con el DTO para pasar el primer IF

        // Simulamos las llamadas a los métodos hermanos dentro del mismo servicio
        doReturn(mockUserDto).when(userServiceSpy).readUser(email);
        doReturn(false).when(userServiceSpy).checkManagerAccess(email); // Forzamos el fallo aquí

        // Act & Assert
        java.nio.file.AccessDeniedException exception = assertThrows(java.nio.file.AccessDeniedException.class, () ->
                userServiceSpy.updateRequest(dto, email)
        );

        assertEquals("Sense permís.", exception.getMessage());
        verifyNoInteractions(requestRepository);
    }

    // RAMA: Flujo feliz. Pasa ambos filtros, modifica la solicitud y guarda.
    @Test
    void updateRequest_Success_ShouldUpdateFieldsAndSave() throws Exception {
        // 1. Convertimos la instancia en un Spy
        UserService userServiceSpy = spy(userService);

        RequestUpdateDTO dto = new RequestUpdateDTO();
        dto.setIdFalla(1L);
        dto.setRequestId(10L);
        dto.setAproved(true);
        dto.setReply("Solicitud aceptada correctamente");

        com.vfortro.gestoreta.dto.users.UserCreateDTO mockUserDto = new com.vfortro.gestoreta.dto.users.UserCreateDTO();
        mockUserDto.setFallaId(1L); // Pasa el primer IF

        // Simulamos el comportamiento de los métodos internos del servicio
        doReturn(mockUserDto).when(userServiceSpy).readUser(email);
        doReturn(true).when(userServiceSpy).checkManagerAccess(email); // Pasa el segundo IF

        // Mockeamos la respuesta del repositorio de solicitudes
        Request mockRequest = new Request();
        mockRequest.setId(10L);
        mockRequest.setAproved(false);
        mockRequest.setReply("Ninguna");

        when(requestRepository.findRequestById(10L)).thenReturn(mockRequest);

        // Act
        userServiceSpy.updateRequest(dto, email);

        // Assert
        assertTrue(mockRequest.getAproved());
        assertEquals("Solicitud aceptada correctamente", mockRequest.getReply());
        verify(requestRepository, times(1)).saveAndFlush(mockRequest);
    }
}