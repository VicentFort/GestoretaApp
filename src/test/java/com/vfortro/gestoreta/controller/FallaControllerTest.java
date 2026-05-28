package com.vfortro.gestoreta.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vfortro.gestoreta.dto.ApiMessageResponse;
import com.vfortro.gestoreta.dto.charges.ChargeUpdateDTO;
import com.vfortro.gestoreta.dto.fallas.FallaCreateDTO;
import com.vfortro.gestoreta.dto.fallas.FallaUpdateDTO;
import com.vfortro.gestoreta.dto.fallas.info.FallaAdminInfoDTO;
import com.vfortro.gestoreta.dto.requests.RequestUpdateDTO;
import com.vfortro.gestoreta.model.enums.AccessType;
import com.vfortro.gestoreta.service.FallaService;
import com.vfortro.gestoreta.service.UserService;

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

@ExtendWith(MockitoExtension.class)
class FallaControllerTest {

    @Mock private FallaService fallaService;
    @Mock private UserService userService;
    @Mock private Authentication authentication;

    @InjectMocks
    private FallaController fallaController;

    private final String userEmail = "fallero_mayor@gestoreta.com";

    @BeforeEach
    void setUp() {
        // Configuramos el mock de autenticación para los métodos que lo requieran
        lenient().when(authentication.getName()).thenReturn(userEmail);
    }

    // ==========================================
    // ENDPOINT: POST /falla/create
    // ==========================================
    @Test
    void postFalla_Success_ShouldReturnCreated() {
        FallaCreateDTO requestDto = new FallaCreateDTO();
        FallaCreateDTO resultDto = new FallaCreateDTO();
        resultDto.setFallaId(1L);

        when(fallaService.createFalla(requestDto)).thenReturn(resultDto);

        ResponseEntity<String> response = fallaController.postFalla(requestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Falla creada con id: 1", response.getBody());
    }

    // ==========================================
    // ENDPOINT: PUT /falla/update
    // ==========================================
    @Test
    void updateFalla_Success_ShouldReturnOk() throws Exception {
        FallaUpdateDTO updateDto = new FallaUpdateDTO();

        ResponseEntity<?> response = fallaController.updateFalla(updateDto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiMessageResponse body = (ApiMessageResponse) response.getBody();
        assertNotNull(body);
        assertEquals("Falla actaulizada.", body.getMessage());
        assertTrue(body.getSuccess());
        verify(fallaService, times(1)).updateFalla(updateDto, userEmail);
    }

    @Test
    void updateFalla_IllegalAccess_ShouldReturnForbidden() throws Exception {
        FallaUpdateDTO updateDto = new FallaUpdateDTO();
        doThrow(new IllegalAccessException("Sin permiso para esta falla.")).when(fallaService).updateFalla(updateDto, userEmail);

        ResponseEntity<?> response = fallaController.updateFalla(updateDto, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Sin permiso para esta falla.", response.getBody());
    }

    // ==========================================
    // ENDPOINT: PUT /falla/updateRequest
    // ==========================================
    @Test
    void updateRequest_NotFound_ShouldReturnNotFound() throws AccessDeniedException, IllegalAccessException {
        RequestUpdateDTO dto = new RequestUpdateDTO();
        dto.setRequestId(5L);

        // Simulamos que el readRequest devuelve null (solicitud inexistente)
        when(userService.readRequest(5L)).thenReturn(null);

        ResponseEntity<?> response = fallaController.updateRequest(dto, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("La sol·licitud no existiex", response.getBody());
        verify(userService, never()).updateRequest(any(), anyString());
    }

    @Test
    void updateRequest_Success_ShouldReturnOk() throws Exception {
        RequestUpdateDTO dto = new RequestUpdateDTO();
        dto.setRequestId(5L);

        // Cambia 'Request.class' por el tipo exacto que devuelva tu método readRequest
        var mockRequest = mock(com.vfortro.gestoreta.dto.requests.RequestUpdateDTO.class);
        when(userService.readRequest(5L)).thenReturn(mockRequest);

        ResponseEntity<?> response = fallaController.updateRequest(dto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiMessageResponse body = (ApiMessageResponse) response.getBody();
        assertNotNull(body);
        assertEquals("Solicitud con id: 5 aceptada", body.getMessage());
    }

    // ==========================================
    // ENDPOINT: POST /falla/addEventTag
    // ==========================================
    @Test
    void addEventTag_Conflict_ShouldReturnConflict() throws Exception {
        String tagName = "Cremà=";
        doThrow(new EntityExistsException("La etiqueta ja existeix")).when(fallaService).addEventTag(userEmail, "Cremà");

        ResponseEntity<?> response = fallaController.addEventTag(tagName, authentication);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("La etiqueta ja existeix", response.getBody());
    }

    @Test
    void addEventTag_Success_ShouldReturnCreated() throws Exception {
        String tagName = "Mascletà";

        ResponseEntity<?> response = fallaController.addEventTag(tagName, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ApiMessageResponse body = (ApiMessageResponse) response.getBody();
        assertEquals("Nueva etiqueta creada con nombre: Mascletà", body.getMessage());
    }

    // ==========================================
    // ENDPOINT: GET /falla/info
    // ==========================================
    @Test
    void getFallaInfo_Success_ShouldReturnDto() throws Exception {
        FallaAdminInfoDTO mockInfo = new FallaAdminInfoDTO();
        when(fallaService.getFallaInfo(userEmail)).thenReturn(mockInfo);

        ResponseEntity<?> response = fallaController.getFallaInfo(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockInfo, response.getBody());
    }

    // ==========================================
    // ENDPOINT: POST /falla/editAccessType
    // ==========================================
    @Test
    void editAdminAccess_AccessDenied_ShouldReturnUnauthorized() throws Exception {
        ChargeUpdateDTO request = new ChargeUpdateDTO();
        request.setUserId(10L);
        request.setAccessType(AccessType.MANAGER);

        doThrow(new AccessDeniedException("No eres administrador global")).when(fallaService).editAccessType(10L, AccessType.MANAGER, userEmail);

        ResponseEntity<?> response = fallaController.editAdminAccess(authentication, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("No eres administrador global", response.getBody());
    }

    @Test
    void editAdminAccess_Success_ShouldReturnOk() throws Exception {
        ChargeUpdateDTO request = new ChargeUpdateDTO();
        request.setUserId(10L);
        request.setAccessType(AccessType.EMPTY_CHARGE);

        ResponseEntity<?> response = fallaController.editAdminAccess(authentication, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Canvis fets", response.getBody());
        verify(fallaService, times(1)).editAccessType(10L, AccessType.EMPTY_CHARGE, userEmail);
    }

    // ==========================================
    // ENDPOINT: DELETE /falla/deleteEventTag
    // ==========================================
    @Test
    void deleteEventTag_NotFound_ShouldReturnNotFound() throws Exception {
        Long tagId = 99L;
        doThrow(new EntityNotFoundException("Etiqueta no trobada")).when(fallaService).deleteEventTag(tagId, userEmail);

        ResponseEntity<?> response = fallaController.deleteEventTag(tagId, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Etiqueta no trobada", response.getBody());
    }

    @Test
    void deleteEventTag_Success_ShouldReturnOk() throws Exception {
        Long tagId = 1L;

        ResponseEntity<?> response = fallaController.deleteEventTag(tagId, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(fallaService, times(1)).deleteEventTag(tagId, userEmail);
    }
}