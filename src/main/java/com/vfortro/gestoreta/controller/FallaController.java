package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.FallaUpdateDto;
import com.vfortro.gestoreta.dto.FallaCreateDto;
import com.vfortro.gestoreta.dto.RequestDto;
import com.vfortro.gestoreta.dto.UserCreateDto;
import com.vfortro.gestoreta.service.FallaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/falla")
public class FallaController {

    @Autowired
    private FallaService fallaService;

    @Tag(name = "falla-controller" ,description = "Obtiene todas las fallas de la BD.")
    @Operation(summary = "Obtiene todas las fallas de la BD.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = FallaCreateDto.class))})
    })
    @GetMapping("/getAll")
    public ResponseEntity<List<FallaCreateDto>> getAll() {
        List<FallaCreateDto> body = fallaService.getAll();
        return new ResponseEntity<>(body,HttpStatus.OK);
    }


    @Operation(summary = "Crea una falla en la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Falla creada", value = "Falla creada con id: 1")
            })}),
    })
    @PostMapping("/create")
    public ResponseEntity<String> postFalla(@Valid @RequestBody FallaCreateDto falla) {
        FallaCreateDto result = fallaService.createFalla(falla);
        return new ResponseEntity<>("Falla creada con id: " + result.getFallaId(), HttpStatus.CREATED);

    }

    @Operation(summary = "Actualiza una falla de la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Falla no encontrada", value = "Falla con id: 1 no encontrada")
            })})
    })
    @PutMapping("/update/{idFalla}")
    public ResponseEntity<String> updateFalla(@Valid @RequestBody FallaUpdateDto newFalla,
                                                @PathVariable @Valid Long idFalla) {
        FallaCreateDto result = fallaService.updateFalla(newFalla, idFalla);
        if(Objects.isNull(result)) {
            return new ResponseEntity<>("Falla con id: " + idFalla + " no encontrada.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Falla actualizada", HttpStatus.OK);
    }

    @Operation(summary = "Busca una falla en la base de datos dado su nombre.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FallaCreateDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Falla no encontrada." , value = "Falla con nombre: FALLA GRAN no econtrada.")
            })})
    })
    @GetMapping("/getByName")
    public ResponseEntity<?> getByName(@RequestParam("fallaName") @Valid String fallaName) {
        FallaCreateDto falla = fallaService.readFalla(fallaName);
        if(Objects.isNull(falla)) {
            return new ResponseEntity<>("Falla con nombre: " + fallaName + " no encontrada.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(falla,HttpStatus.OK);
    }

    @Operation(summary = "Busca una falla en la base de datos dada su id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FallaCreateDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Falla no encontrada." , value = "Falla con id: 1 no econtrada.")
            })})
    })
    @GetMapping("/{fallaId}")
    public ResponseEntity<?> getById(@PathVariable @Valid Long fallaId) {
        FallaCreateDto falla = fallaService.readFalla(fallaId);
        if(Objects.isNull(falla)) {
            return new ResponseEntity<>("Falla con id: " + fallaId + " no encontrada.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(falla, HttpStatus.OK);
    }

    @Operation(summary = "Devuelve los usuarios que pertenencen una falla.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Falla no encontrada." , value = "Falla con id: 1 no econtrada.")
            })})
    })
    @GetMapping("/users/{fallaId}")
    public ResponseEntity<?> getUsers(@PathVariable @Valid Long fallaId) {
        if(Objects.isNull(fallaService.readFalla(fallaId))) {
            return new ResponseEntity<>("Falla con id: " + fallaId + " no encontrada.", HttpStatus.NOT_FOUND);
        }
        List<UserCreateDto> result = fallaService.getUsers(fallaId);
    return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Devuelve las solicitudes de usuarios a una falla.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RequestDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class),examples = {
                    @ExampleObject(name = "Falla no encontrada." , value = "Falla con id: 1 no econtrada.")
            })})
    })
    @GetMapping("/requests/{fallaId}")
    public ResponseEntity<?> getRequests(@PathVariable @Valid Long fallaId) {
        if(Objects.isNull(fallaService.readFalla(fallaId))) {
            return new ResponseEntity<>("Falla con id: " + fallaId + " no encontrada.", HttpStatus.NOT_FOUND);
        }
        List<RequestDto> result = fallaService.getRequests(fallaId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
