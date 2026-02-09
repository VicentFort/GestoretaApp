package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.ApiMessageResponse;
import com.vfortro.gestoreta.dto.FoodNeedCreateDto;
import com.vfortro.gestoreta.dto.UserCreateDto;
import com.vfortro.gestoreta.dto.UserUpdateDto;
import com.vfortro.gestoreta.service.FoodNeedService;
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

    @Autowired
    private FoodNeedService foodNeedService;

    @Operation(summary = "Crea un usuario en la base de datos.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                content = {
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ApiMessageResponse.class
                                ),
                                examples = {
                                        @ExampleObject(name = "Usuario creado.", value = "{\"message\":\"Usuario con id: 1 creado en la base de datos.\",\"success\":true}")

                                })
                })
    })
    @PostMapping("/create")
    public ResponseEntity<?> postUser(@RequestBody @Valid UserCreateDto user) {
        UserCreateDto result = userService.createUser(user);
        return new ResponseEntity<>(new ApiMessageResponse("Usuario con id: " + result.getUserId() + " creado en la base de datos.", true), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualiza un usuario en la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Usuario no encontrado", value = "El usuario con id: 1 no existe.")
            })}),
            @ApiResponse(responseCode = "500", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Actualización fallida", value = "El usuario con id: 1 no se ha podido actualizar")
            })})
    })
    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDto newUser,
                                        @PathVariable @Valid Long userId) {
        if(Objects.isNull(userService.readUser(userId))) {
            return new ResponseEntity<>(new ApiMessageResponse("El usuario con id: " + userId + " no existe.", false), HttpStatus.NOT_FOUND);
        }
        UserCreateDto result = userService.updateUser(newUser, userId);
        if(Objects.isNull(result)) {
            return new ResponseEntity<>(new ApiMessageResponse("El usuario con id: " + userId +" no se ha podido actualizar", false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new ApiMessageResponse("El usuario con id: " + userId + " ha sido actualizado correctamente", true), HttpStatus.OK);
    }

    @Operation(summary = "Busca un usuario en la base de datos dado su nombre.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserCreateDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "text/json", schema = @Schema(implementation =  ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Usuario no encontrado", value = "El usuario con id: 1 no existe.")
            })})
    })
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable @Valid Long userId) {
        UserCreateDto result = userService.readUser(userId);
        if(Objects.isNull(result)) {
            return new ResponseEntity<>(new ApiMessageResponse("El usuario con id: " + userId + " no existe.", false), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Crea una necesidad alimentaria para un usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Necesidad alimentaria creada.", value = "Necesidad alimentaria creada con id 1")
            })})
    })
    @PostMapping("/addFoodNeed")
    public ResponseEntity<?> addFoodNeed(@RequestBody @Valid FoodNeedCreateDto foodNeed) {
        FoodNeedCreateDto result = foodNeedService.createFoodNeed(foodNeed);
        return new ResponseEntity<>(new ApiMessageResponse("Necesidad alimentaria creada con id: " + result.getFoodNeedId(), true), HttpStatus.CREATED);
    }

}
