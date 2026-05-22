package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.users.UserCreateDTO;
import com.vfortro.gestoreta.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;


    @Test
    void createUser_ok() {

        UserCreateDTO dto = new UserCreateDTO();
        dto.setEmail("vicent@email.com");

        when(userService.createUser(any())).thenReturn(dto);

        ResponseEntity<?> response = userController.postUser(dto);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }
}