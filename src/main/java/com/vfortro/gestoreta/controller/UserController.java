package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.*;
import com.vfortro.gestoreta.dto.assists.AssistResultDto;
import com.vfortro.gestoreta.dto.events.AttendantPreferenceInfoDto;
import com.vfortro.gestoreta.dto.events.EventInfoDto;
import com.vfortro.gestoreta.dto.fallas.FallaInfoDto;
import com.vfortro.gestoreta.dto.food.FoodNeedCreateDto;
import com.vfortro.gestoreta.dto.requests.RequestCreateDto;
import com.vfortro.gestoreta.dto.requests.RequestDto;
import com.vfortro.gestoreta.dto.users.UserCreateDto;
import com.vfortro.gestoreta.dto.users.UserInfoDto;
import com.vfortro.gestoreta.dto.users.UserUpdateDto;
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
import java.util.List;
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
            })}),
            @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Permisos inecesarios.", value = "{\"message\":\"Sin permiso.\",\"success\":false}"),
            })})
    })
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDto newUser,
                                        Authentication authentication) {
        String email = authentication.getName();
        try {
            UserInfoDto result = userService.updateUser(newUser, email);
            if(Objects.isNull(result)) {
                return new ResponseEntity<>(new ApiMessageResponse("El usuario no se ha podido actualizar", false), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(),false), HttpStatus.FORBIDDEN);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(),false), HttpStatus.NOT_FOUND);
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
            })}),
            @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Permisos inecesarios.", value = "{\"message\":\"Sin permiso.\",\"success\":false}")
            })})
    })
    @GetMapping("/getUserInfo")
    public ResponseEntity<?> getUserById(Authentication authentication) {
        String email = authentication.getName();
        try {
            UserInfoDto result = userService.readUserSecure(email);
            if(Objects.isNull(result)) {
                return new ResponseEntity<>(new ApiMessageResponse("El usuario no existe.", false), HttpStatus.NOT_FOUND);
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
            })}),
            @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Permisos inecesarios.", value = "{\"message\":\"Sin permiso.\",\"success\":false}")
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
            })}),
            @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Permisos inecesarios.", value = "{\"message\":\"Sin permiso.\",\"success\":false}")
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

    @Tags({
            @Tag(name = "Usuarios"),
            @Tag(name = "Fallas")
    })
    @Operation(summary = "Devuelve información general de la falla del usuario")
    @GetMapping("/falla")
    public ResponseEntity<?> getFallaInfo(Authentication authentication) {
        String email = authentication.getName();
        FallaInfoDto result = userService.getFallaInfo(email);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Tags({
            @Tag(name = "Usuarios"),
            @Tag(name = "Eventos"),
    })
    @Operation(summary = "Añade preferencias para ser escogido para eventos con ciertas etiqueta.")
    @PostMapping("/addAttPrefs")
    public ResponseEntity<?> addAttPrefs(@RequestParam List<Long> tagIds,
                                           Authentication authentication) {
        String email = authentication.getName();
        try {
            userService.createAttPreferences(email, tagIds);
            return new ResponseEntity<>(new ApiMessageResponse("Preferencias guardadas", true), HttpStatus.OK);
        } catch (AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.FORBIDDEN);
        }
    }
    
    @Tags({
            @Tag(name = "Usuarios"),
            @Tag(name = "Eventos"),
    })
    @Operation(summary = "Elimina preferencias para ser escogido para eventos con ciertas etiquetas")
    @DeleteMapping("/removeAttPrefs")
    public ResponseEntity<?> removeAttPrefs(@RequestParam List<Long> prefIds,
                                            Authentication authentication) {
        String email = authentication.getName();
        try {
            userService.removeAttPrefs(email, prefIds);
            return new ResponseEntity<>(new ApiMessageResponse("Eliminadas: " + prefIds.size() + " preferencias.", true), HttpStatus.OK);
        } catch (AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.FORBIDDEN);
        }
    }

    @Tags({
            @Tag(name = "Usuarios")
    })
    @Operation(summary = "Obtiene info de las preferencias de evento del usuario")
    @GetMapping("/getAttPrefs")
    public ResponseEntity<?> getAttPrefs(Authentication authentication) {
        String email = authentication.getName();
        try {
            List<AttendantPreferenceInfoDto> result =userService.getAttPrefs(email);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/events")
    public ResponseEntity<?> getUserEvents(Authentication authentication) {
        String email = authentication.getName();
        try {
            List<EventInfoDto> result = userService.getEvents(email);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
