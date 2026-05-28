package com.vfortro.gestoreta.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vfortro.gestoreta.dto.ApiMessageResponse;
import com.vfortro.gestoreta.dto.users.info.UserInfoDTO;
import com.vfortro.gestoreta.dto.users.UserUpdateDTO;
import com.vfortro.gestoreta.service.UserService;

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
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private UserService userService;
    @Mock private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private final String userEmail = "fallero@gestoreta.com";

    @BeforeEach
    void setUp() {
        lenient().when(authentication.getName()).thenReturn(userEmail);
    }

    // ==========================================
    // ENDPOINT: GET /user/profile o /user/info
    // ==========================================
    @Test
    void getUserProfile_Success_ShouldReturnUserInfo() throws AccessDeniedException {
        UserInfoDTO mockInfo = mock(UserInfoDTO.class);
        when(userService.readUserSecure(userEmail)).thenReturn(mockInfo);

        ResponseEntity<?> response = userController.getUserInfo(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockInfo, response.getBody());
    }




}