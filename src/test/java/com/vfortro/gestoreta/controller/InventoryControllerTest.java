package com.vfortro.gestoreta.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vfortro.gestoreta.dto.inventory.items.InventoryItemCreateDTO;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemInfoDTO;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemUpdateDTO;
import com.vfortro.gestoreta.dto.inventory.loans.LoanInfoDTO;
import com.vfortro.gestoreta.dto.inventory.loans.ReturnLoanDTO;
import com.vfortro.gestoreta.dto.inventory.loans.contacts.LoanContactCreateDTO;
import com.vfortro.gestoreta.dto.inventory.loans.contacts.LoanContactUpdateDTO;
import com.vfortro.gestoreta.dto.inventory.stocks.InventoryMovementRequestDTO;
import com.vfortro.gestoreta.dto.inventory.stocks.InventoryMovementInfoDTO;
import com.vfortro.gestoreta.dto.inventory.stores.StoreCreateDTO;
import com.vfortro.gestoreta.dto.inventory.stores.StoreInfoDTO;
import com.vfortro.gestoreta.dto.inventory.stores.StoreUpdateDTO;
import com.vfortro.gestoreta.exceptions.InsufficientStockException;
import com.vfortro.gestoreta.service.InventoryService;

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
class InventoryControllerTest {

    @Mock private InventoryService inventoryService;
    @Mock private Authentication authentication;

    @InjectMocks
    private InventoryController inventoryController;

    private final String userEmail = "responsable_almacen@falla.com";

    @BeforeEach
    void setUp() {
        lenient().when(authentication.getName()).thenReturn(userEmail);
    }

    // ==========================================
    // ENDPOINT: POST /inv/createStore
    // ==========================================
    @Test
    void createStore_Success_ShouldReturnStoreInfo() throws Exception {
        StoreCreateDTO request = new StoreCreateDTO();
        StoreInfoDTO mockResponse = mock(StoreInfoDTO.class);

        when(inventoryService.createStore(request, userEmail)).thenReturn(mockResponse);

        ResponseEntity<?> response = inventoryController.createStore(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void createStore_NullData_ShouldReturnForbidden() {
        ResponseEntity<?> response = inventoryController.createStore(null, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("La informació del nou magatzem estava incompleta", response.getBody());
    }

    // ==========================================
    // ENDPOINT: POST /inv/createItem
    // ==========================================
    @Test
    void createItem_Success_ShouldReturnItemInfo() throws Exception {
        InventoryItemCreateDTO request = new InventoryItemCreateDTO();
        InventoryItemInfoDTO mockResponse = mock(InventoryItemInfoDTO.class);

        when(inventoryService.createItem(request, userEmail)).thenReturn(mockResponse);

        ResponseEntity<?> response = inventoryController.createItem(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void createItem_Exception_ShouldReturnInternalServerError() throws Exception {
        InventoryItemCreateDTO request = new InventoryItemCreateDTO();
        when(inventoryService.createItem(request, userEmail)).thenThrow(new RuntimeException("Error genérico"));

        ResponseEntity<?> response = inventoryController.createItem(request, authentication);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error genérico", response.getBody());
    }

    // ==========================================
    // ENDPOINT: POST /inv/processMovement
    // ==========================================
    @Test
    void processMovement_InsufficientStock_ShouldReturnInternalServerError() throws Exception {
        InventoryMovementRequestDTO request = new InventoryMovementRequestDTO();
        when(inventoryService.processMovement(request, userEmail))
                .thenThrow(new InsufficientStockException("No queda estoc disponible"));

        ResponseEntity<?> response = inventoryController.processMovement(request, authentication);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("No queda estoc disponible", response.getBody());
    }

    @Test
    void processMovement_Success_ShouldReturnMovementInfo() throws Exception {
        InventoryMovementRequestDTO request = new InventoryMovementRequestDTO();
        InventoryMovementInfoDTO mockResponse = mock(InventoryMovementInfoDTO.class);
        when(inventoryService.processMovement(request, userEmail)).thenReturn(mockResponse);

        ResponseEntity<?> response = inventoryController.processMovement(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    // ==========================================
    // ENDPOINT: POST /inv/returnLoan
    // ==========================================
    @Test
    void returnLoan_IllegalState_ShouldReturnInternalServerError() throws Exception {
        ReturnLoanDTO request = new ReturnLoanDTO();
        when(inventoryService.returnLoan(request, userEmail)).thenThrow(new IllegalStateException("Préstec ja tornat"));

        ResponseEntity<?> response = inventoryController.returnLoan(request, authentication);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Préstec ja tornat", response.getBody());
    }

    // ==========================================
    // ENDPOINT: PUT /inv/updateStore
    // ==========================================
    @Test
    void updateStore_NotFound_ShouldReturnNotFound() throws Exception {
        StoreUpdateDTO request = new StoreUpdateDTO();
        doThrow(new EntityNotFoundException("Magatzem no trobat")).when(inventoryService).updateStore(request, userEmail);

        ResponseEntity<?> response = inventoryController.updateStore(request, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Magatzem no trobat", response.getBody());
    }

    @Test
    void updateStore_Success_ShouldReturnOk() throws Exception {
        StoreUpdateDTO request = new StoreUpdateDTO();

        ResponseEntity<?> response = inventoryController.updateStore(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(inventoryService, times(1)).updateStore(request, userEmail);
    }

    // ==========================================
    // ENDPOINT: PUT /inv/updateItem
    // ==========================================
    @Test
    void updateItem_AccessDenied_ShouldReturnUnauthorized() throws Exception {
        InventoryItemUpdateDTO request = new InventoryItemUpdateDTO();
        doThrow(new AccessDeniedException("Sense permisos d'edició")).when(inventoryService).updateItem(request, userEmail);

        ResponseEntity<?> response = inventoryController.updateItem(request, authentication);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Sense permisos d'edició", response.getBody());
    }

    // ==========================================
    // ENDPOINT: POST /inv/createContact
    // ==========================================
    @Test
    void createContact_Success_ShouldReturnOk() throws Exception {
        LoanContactCreateDTO request = new LoanContactCreateDTO();

        ResponseEntity<?> response = inventoryController.createContact(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(inventoryService, times(1)).createContact(request, userEmail);
    }

    // ==========================================
    // ENDPOINT: PUT /inv/updateContact
    // ==========================================
    @Test
    void updateContact_IllegalAccess_ShouldReturnForbidden() throws Exception {
        LoanContactUpdateDTO request = new LoanContactUpdateDTO();
        doThrow(new IllegalAccessException("Accés no autoritzat")).when(inventoryService).updateContact(request, userEmail);

        ResponseEntity<?> response = inventoryController.updateContact(request, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Accés no autoritzat", response.getBody());
    }
}