package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.RequestConversor;
import com.vfortro.gestoreta.conversor.UserConversor;
import com.vfortro.gestoreta.dto.users.UserCreateDTO;
import com.vfortro.gestoreta.model.Charge;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.model.enums.AccessType;
import com.vfortro.gestoreta.repository.*;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventTagRepository eventTagRepository;

    @Mock
    private AttendandPreferenceRepository attendandPreferenceRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ChargeRepository chargeRepository;

    @Mock
    private FallaRepository fallaRepository;

    @Mock
    private UserConversor userConversor;

    @Mock
    private RequestConversor requestConversor;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserCreateDTO userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = new UserCreateDTO();
        userDto.setEmail("vicent@email.com");
        userDto.setPassword("1234");
        userDto.setBirthday(LocalDate.now().minusYears(20));
        userDto.setAccessType(AccessType.MANAGER);

        user = new User();
        user.setId(1L);
        user.setEmail("vicent@email.com");
    }

    @Test
    void createUser_ShouldSaveUser_WhenDataIsValid() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userConversor.fromDto2Entity(any())).thenReturn(user);
        when(userRepository.saveAndFlush(any())).thenReturn(user);
        when(userConversor.fromEntity2Dto(any())).thenReturn(userDto);

        UserCreateDTO result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals("vicent@email.com", result.getEmail());

        verify(userRepository, times(1)).saveAndFlush(any(User.class));
        verify(chargeRepository, times(1)).saveAndFlush(any(Charge.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EntityExistsException.class,
                () -> userService.createUser(userDto));

        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void readUser_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        when(userConversor.fromEntity2Dto(any()))
                .thenReturn(userDto);

        UserCreateDTO result = userService.readUser("vicent@email.com");

        assertNotNull(result);
        verify(userRepository).findByEmail("vicent@email.com");
    }

    @Test
    void readUser_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.readUser("vicent@email.com"));
    }
}