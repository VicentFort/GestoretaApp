package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.*;
import com.vfortro.gestoreta.dto.requests.RequestCreateDTO;
import com.vfortro.gestoreta.dto.requests.RequestUpdateDTO;
import com.vfortro.gestoreta.dto.users.UserCreateDTO;
import com.vfortro.gestoreta.dto.users.info.UserInfoDTO;
import com.vfortro.gestoreta.dto.users.UserUpdateDTO;
import com.vfortro.gestoreta.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;



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
    public ResponseEntity<?> postUser(@RequestBody @Valid UserCreateDTO user) {
        try {
            UserCreateDTO result = userService.createUser(user);
            return new ResponseEntity<>(new ApiMessageResponse("Usuario con id: " + result.getUserId() + " creado en la base de datos.", true), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(),false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDTO newUser,
                                        Authentication authentication) {
        String email = authentication.getName();
        try {
            UserInfoDTO result = userService.updateUser(newUser, email);
            if(newUser.getName().isBlank()) return new ResponseEntity<>(new ApiMessageResponse("Nom en blanc", false), HttpStatus.INTERNAL_SERVER_ERROR);

            if(newUser.getSurname().isBlank()) return new ResponseEntity<>(new ApiMessageResponse("Cognoms en blanc", false), HttpStatus.INTERNAL_SERVER_ERROR);
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
    @Operation(summary = "Devuelve la info del usuario que está logeado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserCreateDTO.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "text/json", schema = @Schema(implementation =  ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Usuario no encontrado", value = "{\"message\":\"El usuario con id: 1 no existe.\",\"success\":false}")
            })}),
            @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Permisos inecesarios.", value = "{\"message\":\"Sin permiso.\",\"success\":false}")
            })})
    })
    @GetMapping("/getUserInfo")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        String email = authentication.getName();
        try {
            UserInfoDTO result = userService.readUserSecure(email);
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
    public ResponseEntity<?> addFoodNeed(@RequestBody @Valid Map<String, String> payload,
                                         Authentication authentication) {
        String email = authentication.getName();
        String desc = payload.get("desc").toString();
        try {
            UserInfoDTO result = userService.createFoodNeed(desc, email);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch(AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.FORBIDDEN);
        }

    }

    @DeleteMapping("/deleteNeed")
    public ResponseEntity<?> deleteFoodNeed(@RequestParam(name="needId") @Valid Long needId,
                                            Authentication authentication) {
        String email = authentication.getName();
        try {
            UserInfoDTO result = userService.deleteFoodNeed(needId, email);
            return new ResponseEntity<>(result,HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.FORBIDDEN);
        }
    }


    @Tags({
            @Tag(name = "Usuarios"),
            @Tag(name = "Eventos"),
    })
    @Operation(summary = "Añade preferencias para ser escogido para eventos con ciertas etiqueta.")
    @PostMapping("/addAttPrefs")
    public ResponseEntity<?> addAttPrefs(@RequestBody Long tagId,
                                           Authentication authentication) {
        String email = authentication.getName();
        try {
            userService.createAttPreferences(email, tagId);
            return new ResponseEntity<>(new ApiMessageResponse("Preferencias guardadas", true), HttpStatus.OK);
        } catch (AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.FORBIDDEN);
        } catch(EntityExistsException ex) {
            return new ResponseEntity<>(new ApiMessageResponse(ex.getMessage(), false), HttpStatus.CONFLICT);
        }catch (Exception any) {
            return new ResponseEntity<>(new ApiMessageResponse(any.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Tags({
            @Tag(name = "Usuarios"),
            @Tag(name = "Eventos"),
    })
    @Operation(summary = "Elimina preferencias para ser escogido para eventos con ciertas etiquetas")
    @DeleteMapping("/removeAttPrefs")
    public ResponseEntity<?> removeAttPrefs(@RequestBody List<Long> prefIds,
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
    public ResponseEntity<?> createRequest(@RequestBody @Valid RequestCreateDTO requestDto,
                                           Authentication authentication) {
        String email = authentication.getName();
        try {
            RequestUpdateDTO result = userService.createRequest(requestDto, email);
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
