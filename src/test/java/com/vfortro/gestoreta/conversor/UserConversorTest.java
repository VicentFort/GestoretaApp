package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.users.UserCreateDTO;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.repository.FallaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserConversorTest {

    @Mock
    private FallaRepository fallaRepository;

    @InjectMocks
    private UserConversor userConversor;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Vicent");
        user.setSurname("Fortro");
        user.setNickname("vfortro");
    }

    @Test
    void fromEntity2Dto_ShouldMapCorrectly() {
        UserCreateDTO dto = userConversor.fromEntity2Dto(user);

        assertNotNull(dto);
        assertEquals(user.getId(), dto.getUserId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getSurname(), dto.getSurname());
        assertEquals(user.getNickname(), dto.getNickname());
    }

    @Test
    void fromDto2Entity_ShouldMapCorrectly() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUserId(1L);
        dto.setName("Vicent");
        dto.setSurname("Fort Tronch");

        when(fallaRepository.existsById(anyLong())).thenReturn(false);

        User entity = userConversor.fromDto2Entity(dto);

        assertNotNull(entity);
        assertEquals(dto.getUserId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
    }
}