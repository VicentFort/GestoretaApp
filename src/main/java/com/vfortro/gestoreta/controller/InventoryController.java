package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.ApiMessageResponse;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemCreateDto;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemInfoDto;
import com.vfortro.gestoreta.dto.inventory.loans.ReturnLoanDto;
import com.vfortro.gestoreta.dto.inventory.stocks.InventoryMovementDto;
import com.vfortro.gestoreta.dto.inventory.stocks.InventoryMovementResultDto;
import com.vfortro.gestoreta.dto.inventory.stores.StoreCreateDto;
import com.vfortro.gestoreta.dto.inventory.stores.StoreInfoDto;
import com.vfortro.gestoreta.exceptions.InsufficientStockException;
import com.vfortro.gestoreta.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> createStore(@RequestBody StoreCreateDto newStore,
                                         Authentication auth) {
        String email = auth.getName();
        try {
            if(Objects.nonNull(newStore) && Objects.nonNull(email)) {
                StoreInfoDto result = inventoryService.createStore(newStore,email);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            return new ResponseEntity<>(new ApiMessageResponse("La informació del nou magatzem estava incompleta", false), HttpStatus.FORBIDDEN);
        } catch (AccessDeniedException ex) {
            return new ResponseEntity<>(new ApiMessageResponse(ex.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Tags({
            @Tag(name = "Inventari"),
            @Tag(name ="Creación")
    })
    @Operation(summary="Crea un item en la base de dades")
    @PostMapping("/createItem")
    public ResponseEntity<?> createItem(@RequestBody InventoryItemCreateDto newItem,
                                        Authentication auth) {
        String email = auth.getName();
        try {
            InventoryItemInfoDto result = inventoryService.createItem(newItem, email);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(AccessDeniedException ex) {
            return new ResponseEntity<>(new ApiMessageResponse(ex.getMessage(), false), HttpStatus.FORBIDDEN);
        } catch(Exception ex) {
            return new ResponseEntity<>(new ApiMessageResponse(ex.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/processMovement")
    public ResponseEntity<?> processMovement(@RequestBody InventoryMovementDto movement,
                                              Authentication auth) {
        String email = auth.getName();
        try {
            InventoryMovementResultDto result = inventoryService.processMovement(movement, email);
            return new ResponseEntity<>(result,HttpStatus.OK);
        } catch(AccessDeniedException ex) {
            return new ResponseEntity<>(new ApiMessageResponse(ex.getMessage(), false), HttpStatus.FORBIDDEN);
        } catch(InsufficientStockException ex) {
            return new ResponseEntity<>(new ApiMessageResponse(ex.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/returnLoan")
    public ResponseEntity<?> returnLoan(@RequestBody ReturnLoanDto returnDto,
                                        Authentication auth) {
        String email = auth.getName();
        try {
            inventoryService.returnLoan(returnDto, email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(AccessDeniedException ex) {
            return new ResponseEntity<>(new ApiMessageResponse(ex.getMessage(), false), HttpStatus.FORBIDDEN);
        }
    }

}
