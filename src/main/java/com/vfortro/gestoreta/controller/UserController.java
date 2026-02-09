package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.UserCreateDto;
import com.vfortro.gestoreta.dto.UserUpdateDto;
import com.vfortro.gestoreta.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Crea un usuario en la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
    })
    @PostMapping("/create")
    public ResponseEntity<?> postUser(@RequestBody @Valid UserCreateDto user) {
        userService.createUser(user);
        return new ResponseEntity<>("Usuario creado en la base de datos.", HttpStatus.CREATED);
    }

    @Operation(summary = "Actualiza un usuario en la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Usuario no encontrado", value = "El usuario con id: 1 no existe.")
            })}),
            @ApiResponse(responseCode = "500", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Actualización fallida", value = "El usuario con id: 1 no se ha podido actualizar")
            })})
    })
    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserUpdateDto newUser,
                                        @PathVariable @Valid Long userId) {
        if(Objects.isNull(userService.readUser(userId))) {
            return new ResponseEntity<>("El usuario con id: " + userId + " no existe.", HttpStatus.NOT_FOUND);
        }
        UserCreateDto result = userService.updateUser(newUser, userId);
        if(Objects.isNull(result)) {
            return new ResponseEntity<>("El usuario con id: " + userId +" no se ha podido actualizar", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("El usuario con id: " + userId + " ha sido actualizado correctamente", HttpStatus.OK);
    }

    @Operation(summary = "Busca un usuario en la base de datos dado su nombre.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserCreateDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "text/json", schema = @Schema(implementation =  String.class), examples = {
                    @ExampleObject(name = "Usuario no encontrado", value = "El usuario con id: 1 no existe.")
            })})
    })
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable @Valid Long userId) {
        UserCreateDto result = userService.readUser(userId);
        if(Objects.isNull(result)) {
            return new ResponseEntity<>("El usuario con id: " + userId + " no existe.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
