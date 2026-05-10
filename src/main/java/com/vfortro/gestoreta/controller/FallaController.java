package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.*;
import com.vfortro.gestoreta.dto.fallas.info.FallaAdminInfoDTO;
import com.vfortro.gestoreta.dto.fallas.FallaCreateDTO;
import com.vfortro.gestoreta.dto.fallas.FallaUpdateDTO;
import com.vfortro.gestoreta.dto.requests.RequestUpdateDTO;
import com.vfortro.gestoreta.model.enums.AccessType;
import com.vfortro.gestoreta.service.FallaService;
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
import java.util.Objects;

@RestController
@RequestMapping("/falla")
public class FallaController {

    @Autowired
    private FallaService fallaService;

    @Autowired
    private UserService userService;


    @Tags({
            @Tag(name = "Creación"),
            @Tag(name = "Fallas")
    })
    @Operation(summary = "Crea una falla en la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Falla creada", value = "Falla creada con id: 1")
            })}),
    })
    @PostMapping("/create")
    public ResponseEntity<String> postFalla(@Valid @RequestBody FallaCreateDTO falla) {
        FallaCreateDTO result = fallaService.createFalla(falla);
        return new ResponseEntity<>("Falla creada con id: " + result.getFallaId(), HttpStatus.CREATED);

    }

    @Tags({
            @Tag(name = "Actualización"),
            @Tag(name = "Fallas")
    })
    @Operation(summary = "Actualiza una falla de la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Falla no encontrada", value = "{\"message\":\"Falla con id: 1 no encontrada\",\"success\":false}")
            })}),
            @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Permisos inecesarios.", value = "{\"message\":\"Sin permiso.\",\"success\":false}"),
                    @ExampleObject(name = "Falla incorrecta.", value = "{\"message\":\"Sin permiso para esta falla.\",\"success\":false}")
            })})
    })
    @PutMapping("/update")
    public ResponseEntity<?> updateFalla(@Valid @RequestBody FallaUpdateDTO newFalla,
                                         Authentication authentication)
    {
        String email = authentication.getName();
        try {
            fallaService.updateFalla(newFalla, email);
            return new ResponseEntity<>(new ApiMessageResponse("Falla actaulizada.",true), HttpStatus.OK);
        } catch(AccessDeniedException accEx) {
            return new ResponseEntity<>( new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.UNAUTHORIZED);
        } catch(NullPointerException nullEx) {
            return new ResponseEntity<>( new ApiMessageResponse(nullEx.getMessage(), false), HttpStatus.NOT_FOUND);
        } catch(IllegalAccessException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @Tags({
            @Tag(name = "Actualización"),
            @Tag(name = "Solicitudes"),
            @Tag(name = "Usuarios"),
            @Tag(name = "Fallas")
    })
    @Operation(summary = "Cambia el estado de una solicitud de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Solicitud no encontrada", value ="{\"message\":\"La solicitud con id: 1 no existe.\",\"success\":false}")
            })}),
            @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Permisos inecesarios.", value = "{\"message\":\"Sin permiso.\",\"success\":false}"),
            })})
    })
    @PutMapping("/updateRequest")
    public ResponseEntity<?> updateRequest(@RequestBody @Valid RequestUpdateDTO dto,
                                           Authentication authentication) {
        String email = authentication.getName();
        if(Objects.isNull(userService.readRequest(dto.getRequestId()))) {
            return new ResponseEntity<>(new ApiMessageResponse("La solicitud de unión con id: " + dto.getRequestId() + " no existe.", false), HttpStatus.NOT_FOUND);
        }
        try {
            userService.updateRequest(dto, email);
            return new ResponseEntity<>(new ApiMessageResponse("Solicitud con id: "+ dto.getRequestId() +" aceptada",true), HttpStatus.OK);
        } catch(AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.UNAUTHORIZED);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }


    @Tags({
            @Tag(name = "Eventos"),
            @Tag(name = "Fallas")
    })
    @Operation(summary = "Crea una etiqueta de evento")
    @PostMapping("/addEventTag")
    public ResponseEntity<?> addEventTag(@RequestBody String name,
                                         Authentication authentication) {
        String email = authentication.getName();
        String nameSolved = name.replace("=", "");
        try {
            fallaService.addEventTag(email, nameSolved);
            return new ResponseEntity<>(new ApiMessageResponse("Nueva etiqueta creada con nombre: " + name, true), HttpStatus.CREATED);
        } catch (AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException notFoundEx) {
            return new ResponseEntity<>(new ApiMessageResponse(notFoundEx.getMessage(), false), HttpStatus.NOT_FOUND);
        } catch (EntityExistsException conflictEx) {
            return new ResponseEntity<>(new ApiMessageResponse(conflictEx.getMessage(), false), HttpStatus.CONFLICT);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }


    @GetMapping("/info")
    public ResponseEntity<?> getFallaInfo(Authentication authentication) {
        String email = authentication.getName();
        try {
            FallaAdminInfoDTO result = fallaService.getFallaInfo(email);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }
    @PostMapping("/editAdminAccess/{userId}")
    public ResponseEntity<?> editAdminAccess(Authentication authentication,
                                              @RequestBody AccessType access,
                                             @PathVariable Long userId) {
        String email = authentication.getName();
        try {
            fallaService.editAccessType(userId,access, email);
            return new ResponseEntity<>("Canvis fets", HttpStatus.OK);
        } catch(EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch(AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(IllegalAccessException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/deleteEventTag")
    public ResponseEntity<?> deleteEventTag(@RequestBody Long tagId,
                                            Authentication authentication) {
        String email = authentication.getName();
        try {
            fallaService.deleteEventTag(tagId, email);
            return new ResponseEntity<>(HttpStatus.OK);
        }  catch(EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch(AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(IllegalAccessException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

}
