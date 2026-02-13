package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.*;
import com.vfortro.gestoreta.service.FallaService;
import com.vfortro.gestoreta.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
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
@RequestMapping("/falla")
public class FallaController {

    @Autowired
    private FallaService fallaService;

    @Autowired
    private RequestService requestService;

    @Tags({
            @Tag(name = "Filtrado"),
            @Tag(name = "Fallas")
    })
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
    public ResponseEntity<String> postFalla(@Valid @RequestBody FallaCreateDto falla) {
        FallaCreateDto result = fallaService.createFalla(falla);
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
    @PutMapping("/update/{idFalla}")
    public ResponseEntity<?> updateFalla(@Valid @RequestBody FallaUpdateDto newFalla,
                                         @PathVariable @Valid Long idFalla,
                                         Authentication authentication)
    {
        String email = authentication.getName();
        if(Objects.isNull(fallaService.readFalla(idFalla))) return new ResponseEntity<>(new ApiMessageResponse("La falla con id: " + idFalla + " no existe.", false),HttpStatus.NOT_FOUND);
        try {
            fallaService.updateFalla(newFalla, idFalla, email);
            return new ResponseEntity<>(new ApiMessageResponse("Falla actaulizada.",true), HttpStatus.OK);
        } catch(AccessDeniedException accEx) {
            return new ResponseEntity<>( new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.FORBIDDEN);
        }
    }

    @Tags({
            @Tag(name = "Filtrado"),
            @Tag(name = "Fallas")
    })
    @Operation(summary = "Busca una falla en la base de datos dado su nombre.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FallaCreateDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Falla no encontrada." , value = "{\"message\":\"Falla con nombre: FALLA GRAN no econtrada.\",\"success\":false}")
            })})
    })
    @GetMapping("/getByName")
    public ResponseEntity<?> getByName(@RequestParam("fallaName") @Valid String fallaName) {
        FallaCreateDto falla = fallaService.readFalla(fallaName);
        if(Objects.isNull(falla)) {
            return new ResponseEntity<>(new ApiMessageResponse("Falla con nombre: " + fallaName + " no encontrada.", false), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(falla,HttpStatus.OK);
    }

    @Tags({
            @Tag(name = "Filtrado"),
            @Tag(name = "Fallas")
    })
    @Operation(summary = "Busca una falla en la base de datos dada su id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FallaCreateDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Falla no encontrada." , value = "{\"message\":\"Falla con id: 1 no econtrada.\",\"success\":false}")
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

    @Tags({
            @Tag(name = "Filtrado"),
            @Tag(name = "Usuarios"),
            @Tag(name = "Fallas")
    })
    @Operation(summary = "Devuelve los usuarios que pertenencen una falla.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Falla no encontrada." , value = "{\"message\":\"Falla con id: 1 no econtrada.\",\"success\":false}")
            })}),
            @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Permisos inecesarios.", value = "{\"message\":\"Sin permiso.\",\"success\":false}"),
                    @ExampleObject(name = "Falla incorrecta.", value = "{\"message\":\"Sin permiso para esta falla.\",\"success\":false}")
            })})
    })
    @GetMapping("/users/{fallaId}")
    public ResponseEntity<?> getUsers(@PathVariable @Valid Long fallaId,
                                      Authentication authentication) {
        String email = authentication.getName();
        if(Objects.isNull(fallaService.readFalla(fallaId))) {
            return new ResponseEntity<>(new ApiMessageResponse("Falla con id: " + fallaId + " no encontrada.", false), HttpStatus.NOT_FOUND);
        }
        try {
            List<UserCreateDto> result = fallaService.getUsers(fallaId, email);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (AccessDeniedException accEx) {
        return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(),false), HttpStatus.FORBIDDEN);
        }

    }

    @Tags({
            @Tag(name = "Filtrado"),
            @Tag(name = "Solicitudes"),
            @Tag(name = "Usuarios"),
            @Tag(name = "Fallas")
    })
    @Operation(summary = "Devuelve las solicitudes de usuarios a una falla.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RequestDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class),examples = {
                    @ExampleObject(name = "Falla no encontrada." , value = "{\"message\":\"Falla con id: 1 no econtrada.\",\"success\":false}")
            })}),
            @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Permisos inecesarios.", value = "{\"message\":\"Sin permiso.\",\"success\":false}"),
                    @ExampleObject(name = "Falla incorrecta.", value = "{\"message\":\"Sin permiso para esta falla.\",\"success\":false}")
            })})
    })
    @GetMapping("/requests/{fallaId}")
    public ResponseEntity<?> getRequests(@PathVariable @Valid Long fallaId,
                                         Authentication authentication) {
        String email = authentication.getName();
        if(Objects.isNull(fallaService.readFalla(fallaId))) {
            return new ResponseEntity<>(new ApiMessageResponse("Falla con id: " + fallaId + " no encontrada.", false), HttpStatus.NOT_FOUND);
        }
        try {
            List<RequestDto> result = fallaService.getRequests(fallaId,email);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(),false),HttpStatus.FORBIDDEN);
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
    public ResponseEntity<?> acceptRequest(@RequestBody @Valid RequestDto dto,
                                           Authentication authentication) {
        String email = authentication.getName();
        if(Objects.isNull(requestService.readRequest(dto.getRequestId()))) {
            return new ResponseEntity<>(new ApiMessageResponse("La solicitud de unión con id: " + dto.getRequestId() + " no existe.", false), HttpStatus.NOT_FOUND);
        }
        try {
            requestService.updateRequest(dto, email);
            return new ResponseEntity<>(new ApiMessageResponse("Solicitud con id: "+ dto.getRequestId() +" aceptada",true), HttpStatus.OK);
        } catch(AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.FORBIDDEN);
        }

    }

}
