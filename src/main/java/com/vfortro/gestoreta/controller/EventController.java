package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.*;
import com.vfortro.gestoreta.dto.assists.AssistDTO;
import com.vfortro.gestoreta.dto.events.EventCreateDTO;
import com.vfortro.gestoreta.dto.events.EventUpdateDTO;
import com.vfortro.gestoreta.repository.EventRepository;
import com.vfortro.gestoreta.service.EventService;
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
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository testRepo;

    @GetMapping("/testRevenue")
    public ResponseEntity<?> getTestRevenue(Authentication auth) {
        eventService.getTotalRevenue();
        return ResponseEntity.ok(testRepo.findByTotalRevenueIsNotNullAndInactiveAndWithPrice().size());
    }

    @Tags({
            @Tag(name = "Creación"),
            @Tag(name = "Eventos")
    })
    @Operation(summary = "Crea un evento en la base de datos.", description = "Falla en caso de que exista un evento con la misma Id.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Evento creado", value = "{\"message\":\"Evento creado\",\"success\":true}")
            })}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Falla inexistente", value = "{\"message\":\"La falla a la que se está asociando el evento: GRAN EVENTO no existe\",\"success\":false}"),
                    @ExampleObject(name = "Etiqueta de evento inexistente", value = "{\"message\":\"La etiqueta a la que se está asociando el evento: GRAN EVENTO no existe\",\"success\":false}"),
            })}),
            @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Permisos inecesarios.", value = "{\"message\":\"Sin permiso.\",\"success\":false}"),
                    @ExampleObject(name = "Falla incorrecta.", value = "{\"message\":\"Sin permiso para esta falla.\",\"success\":false}")
            })})
    })
    @PostMapping("/create")
    public ResponseEntity<?> postEvent(@Valid @RequestBody EventCreateDTO event,
                                       Authentication auth) {
        String email = auth.getName();
        try {
            if(event.getEndHour().isBefore(event.getStartHour()) || event.getStartHour().isAfter(event.getEndHour())) return new ResponseEntity<>("L'esdeveniment té horaris incorrectes", HttpStatus.FORBIDDEN);
            EventCreateDTO result = eventService.createEvent(event, email);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch(EntityNotFoundException entityEx) {
            return new ResponseEntity<>(entityEx.getMessage(), HttpStatus.NOT_FOUND);
        }  catch (AccessDeniedException accEx) {
            return new ResponseEntity<>(accEx.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @Tags({
            @Tag(name = "Eliminación"),
            @Tag(name = "Eventos")
    })
    @Operation(summary = "Elimina un evento de la base de datos dada su id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Evento inexsitente", value = "{\"message\":\"El evento a eliminar no existe.\",\"success\":false}" )
            })}),
            @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Permisos inecesarios.", value = "{\"message\":\"Sin permiso.\",\"success\":false}"),
                    @ExampleObject(name = "Falla incorrecta.", value = "{\"message\":\"Sin permiso para esta falla.\",\"success\":false}")
            })})
    })
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable @Valid Long eventId,
                                         Authentication authentication) {
        String email = authentication.getName();
        try {
            eventService.deleteEvent(eventId, email);
            return new ResponseEntity<>(new ApiMessageResponse("Esdeveniment eliminat",true), HttpStatus.OK);
        } catch(AccessDeniedException accEx) {
            return new ResponseEntity<>(accEx.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @Tags({
            @Tag(name = "Actualización"),
            @Tag(name = "Eventos")
    })
    @Operation(summary = "Actualiza un evento en la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Evento no encontrado", value = "{\"message\":\"El evento con id: 1 no existe.\",\"success\":false}")
            })}),
            @ApiResponse(responseCode = "500", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Error al actualizar", value = "{\"message\":\"El evento con id: 1 no se ha podido actualizar.\",\"success\":false}")
            })}),
            @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema = @Schema(implementation =  ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Permisos inecesarios.", value = "{\"message\":\"Sin permiso.\",\"success\":false}"),
                    @ExampleObject(name = "Falla incorrecta.", value = "{\"message\":\"Sin permiso para esta falla.\",\"success\":false}")
            })})
    })
    @PutMapping("/update")
    public ResponseEntity<?> updateEvent(@RequestBody @Valid EventUpdateDTO newEvent,
                                         Authentication authentication) {
        String email = authentication.getName();
        try {
            EventCreateDTO result = eventService.updateEvent(newEvent, email);
            return new ResponseEntity<>("Esdeveniment actualitzat.", HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException | IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @Tags({
            @Tag(name = "Creación"),
            @Tag(name = "Asistencias")
    })
    @Operation(summary = "Apunta un usuario a un evento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                @ExampleObject(name = "Asistencia inexistente.", value = "La asistencia del usuario con id: 1 al evento con id: 1 ya existe.")
            })})
    })
    @PostMapping("/join/{eventId}")
    public ResponseEntity<?> joinEvent(@PathVariable @Valid Long eventId,
                                       Authentication authentication) {
        String email = authentication.getName();
        try {
            AssistDTO result = eventService.createAssist(email, eventId);
            return new ResponseEntity<>(new ApiMessageResponse("Asistencia creada con id: " +  result.getAssistId(), true), HttpStatus.CREATED);
        } catch(EntityExistsException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        } catch(EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @Tags({
            @Tag(name = "Eliminación"),
            @Tag(name = "Asistencias")
    })
    @Operation(summary = "Elimina la asistencia de un usuario a un evento.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Asistencia inexistente.", value = "La asistencia del usuario con id: 1 al evento con id: 1 no existe.")
            })})
    })
    @DeleteMapping("/leave/{assistId}")
    public ResponseEntity<?> leaveEvent(@PathVariable @Valid Long assistId,
                                        Authentication authentication) {
        String email = authentication.getName();
        try {
            eventService.deleteAssist(assistId, email);
            return new ResponseEntity<>("OK!", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }



}
