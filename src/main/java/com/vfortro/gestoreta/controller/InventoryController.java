package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.ApiMessageResponse;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@RestController
@RequestMapping("/inv")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;
    @Tags({
            @Tag(name = "Inventari"),
            @Tag(name = "Creación")
    })
    @Operation(summary = "Crea un magatzem en la base de dades")
    @PostMapping("/createStore")
    public ResponseEntity<?> createStore(@RequestBody StoreCreateDTO newStore,
                                         Authentication auth) {
        String email = auth.getName();
        try {
            if(Objects.nonNull(newStore) && Objects.nonNull(email)) {
                StoreInfoDTO result = inventoryService.createStore(newStore,email);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            return new ResponseEntity<>("La informació del nou magatzem estava incompleta", HttpStatus.FORBIDDEN);
        } catch (AccessDeniedException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @Tags({
            @Tag(name = "Inventari"),
            @Tag(name ="Creación")
    })
    @Operation(summary="Crea un item en la base de dades")
    @PostMapping("/createItem")
    public ResponseEntity<?> createItem(@RequestBody InventoryItemCreateDTO newItem,
                                        Authentication auth) {
        String email = auth.getName();
        try {
            InventoryItemInfoDTO result = inventoryService.createItem(newItem, email);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(AccessDeniedException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/processMovement")
    public ResponseEntity<?> processMovement(@RequestBody InventoryMovementRequestDTO movement,
                                              Authentication auth) {
        String email = auth.getName();
        try {
            InventoryMovementInfoDTO result = inventoryService.processMovement(movement, email);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(AccessDeniedException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(InsufficientStockException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @PostMapping("/returnLoan")
    public ResponseEntity<?> returnLoan(@RequestBody ReturnLoanDTO returnDto,
                                        Authentication auth) {
        String email = auth.getName();
        try {
            LoanInfoDTO result = inventoryService.returnLoan(returnDto, email);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(AccessDeniedException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(IllegalStateException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }


    @PutMapping("/updateStore")
    public ResponseEntity<?> updateStore(@RequestBody StoreUpdateDTO updatedStore,
                                         Authentication auth) {
        String email = auth.getName();
        try {
            inventoryService.updateStore(updatedStore, email);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (AccessDeniedException ex) {
            return new  ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }


    @PutMapping("/updateItem")
    public ResponseEntity<?> updateItem(@RequestBody InventoryItemUpdateDTO updatedItem,
                                        Authentication auth) {
        String email = auth.getName();
        try {
            inventoryService.updateItem(updatedItem, email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccessDeniedException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/createContact")
    public ResponseEntity<?> createContact(@RequestBody LoanContactCreateDTO contact,
                                           Authentication auth) {
        String email = auth.getName();
        try {
            inventoryService.createContact(contact,email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new  ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/updateContact")
    public ResponseEntity<?> updateContact(@RequestBody LoanContactUpdateDTO contact,
                                           Authentication auth) {
        String email = auth.getName();
        try {
            inventoryService.updateContact(contact,email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(AccessDeniedException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }


}
