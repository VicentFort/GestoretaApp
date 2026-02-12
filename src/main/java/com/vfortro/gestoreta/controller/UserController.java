package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.*;
import com.vfortro.gestoreta.service.FoodNeedService;
import com.vfortro.gestoreta.service.RequestService;
import com.vfortro.gestoreta.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FoodNeedService foodNeedService;

    @Autowired
    private RequestService requestService;

    @Tags({
            @Tag(name = "Creación"),
            @Tag(name = "Usuarios")
    })
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

    @Tags({
            @Tag(name = "Actualización"),
            @Tag(name = "Usuarios")
    })
    @Operation(summary = "Actualiza un usuario en la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Usuario no encontrado", value = "{\\\"message\\\":\\\"El usuario con id: 1 no existe.\\\",\\\"success\\\":false}\"")
            })}),
            @ApiResponse(responseCode = "500", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Actualización fallida", value = "{\"message\":\"El usuario con id: 1 no se ha podido actualizar\",\"success\":false}")
            })})
    })
    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDto newUser,
                                        @PathVariable @Valid Long userId,
                                        Authentication authentication) {
        String email = authentication.getName();
        if(Objects.isNull(userService.readUser(userId))) {
            return new ResponseEntity<>(new ApiMessageResponse("El usuario con id: " + userId + " no existe.", false), HttpStatus.NOT_FOUND);
        }
        try {
            UserCreateDto result = userService.updateUser(newUser, userId, email);
            if(Objects.isNull(result)) {
                return new ResponseEntity<>(new ApiMessageResponse("El usuario con id: " + userId +" no se ha podido actualizar", false), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(new ApiMessageResponse("El usuario con id: " + userId + " ha sido actualizado correctamente", true), HttpStatus.OK);
        } catch (AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(),false), HttpStatus.FORBIDDEN);
        }

    }

    @Tags({
            @Tag(name = "Filtrado"),
            @Tag(name = "Usuarios")
    })
    @Operation(summary = "Busca un usuario en la base de datos dado su nombre.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserCreateDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "text/json", schema = @Schema(implementation =  ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Usuario no encontrado", value = "{\"message\":\"El usuario con id: 1 no existe.\",\"success\":false}")
            })})
    })
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable @Valid Long userId,
                                         Authentication authentication) {
        String email = authentication.getName();
        try {
            UserCreateDto result = userService.readUserSecure(userId, email);
            if(Objects.isNull(result)) {
                return new ResponseEntity<>(new ApiMessageResponse("El usuario con id: " + userId + " no existe.", false), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.FORBIDDEN);
        }

    }

    @Tags({
            @Tag(name = "Creación"),
            @Tag(name = "N. Alimentarias"),
            @Tag(name = "Usuarios")
    })
    @Operation(summary = "Crea una necesidad alimentaria para un usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Necesidad alimentaria creada.", value = "{\"message\":\"Necesidad alimentaria creada con id 1\",\"success\":false}")
            })})
    })
    @PostMapping("/addFoodNeed")
    public ResponseEntity<?> addFoodNeed(@RequestBody @Valid FoodNeedCreateDto foodNeed,
                                         Authentication authentication) {
        String email = authentication.getName();
        try {
            FoodNeedCreateDto result = foodNeedService.createFoodNeed(foodNeed, email);
            return new ResponseEntity<>(new ApiMessageResponse("Necesidad alimentaria creada con id: " + result.getFoodNeedId(), true), HttpStatus.CREATED);
        } catch(AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.FORBIDDEN);
        }

    }

    @Tags({
            @Tag(name = "Creación"),
            @Tag(name = "Solicitudes"),
            @Tag(name = "Fallas"),
            @Tag(name = "Usuarios")
    })
    @Operation(summary = "Crea una solicitud de un usuario a una falla")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class))}),
            @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Dto incompleto.", value = "{\"message\":\"La solicitud debe tener una id de usuario.\",\"success\":false}")
            })}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Usuario no encontrado", value = "{\"message\":\"El usuario con id: 1 no existe.\",\"success\":false}"),
                    @ExampleObject(name = "Falla no encontrada", value = "{\"message\":\"La falla con id: 1 no existe.\",\"success\":false}")
            })}),
            @ApiResponse(responseCode = "500", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Conflicto usuario/falla", value = "{\"message\":\"El usuario con id: 1 ya está en una falla\",\"success\":false}")
            })})
    })
    @PostMapping("/createRequest")
    public ResponseEntity<?> createRequest(@RequestBody @Valid RequestCreateDto requestDto,
                                           Authentication authentication) {
        String email = authentication.getName();
        try {
            RequestDto result = requestService.createRequest(requestDto, email);
            return new ResponseEntity<>(new ApiMessageResponse("Solicitud creada con id: " + result.getRequestId(), false), HttpStatus.CREATED);
        } catch(NullPointerException nullEx) {
            return new ResponseEntity<>(new ApiMessageResponse(nullEx.getMessage(), false), HttpStatus.FORBIDDEN);
        } catch(EntityNotFoundException entEx) {
            return new ResponseEntity<>(new ApiMessageResponse(entEx.getMessage(), false), HttpStatus.NOT_FOUND);
        } catch(IllegalAccessException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.FORBIDDEN);
        }
    }



}
