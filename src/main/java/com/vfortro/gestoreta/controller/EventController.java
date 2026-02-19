package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.*;
import com.vfortro.gestoreta.dto.assists.AssistDto;
import com.vfortro.gestoreta.dto.assists.AssistResultDto;
import com.vfortro.gestoreta.dto.events.EventCreateDto;
import com.vfortro.gestoreta.dto.events.EventFilter;
import com.vfortro.gestoreta.dto.events.EventUpdateDto;
import com.vfortro.gestoreta.dto.food.FoodNeedResultDto;
import com.vfortro.gestoreta.service.AssistService;
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
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private AssistService assistService;

    @Tags({
            @Tag(name = "Filtrado"),
            @Tag(name = "Eventos")
    })

    @Operation(summary = "Busca un evento en la base de datos dada su Id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventCreateDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Evento no encontrado", value = "{\"message\":\"Evento no encontrado\",\"success\":false}"  )
            })})
    })
    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable @Valid Long eventId,
                                          Authentication authentication) {
        String email = authentication.getName();
        EventCreateDto eventCreateDto = eventService.readEvent(eventId);
        if(!Objects.nonNull(eventCreateDto)) {
            return new ResponseEntity<>(new ApiMessageResponse("Evento no encontrado", false), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(eventCreateDto,HttpStatus.OK);
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
    public ResponseEntity<?> postEvent(@Valid @RequestBody EventCreateDto event,
                                       Authentication auth) {
        String email = auth.getName();
        try {
            eventService.createEvent(event, email);
            return new ResponseEntity<>(new ApiMessageResponse("Evento creado.", true), HttpStatus.CREATED);
        } catch(EntityNotFoundException entityEx) {
            return new ResponseEntity<>(new ApiMessageResponse(entityEx.getMessage(), false), HttpStatus.NOT_FOUND);
        }  catch (AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.FORBIDDEN);
        }
    }

    @Tags({
        @Tag(name = "Filtrado"),
        @Tag(name = "Eventos")
    })
    @Operation(summary = "Filtra los eventos de una falla")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventCreateDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "No se han encontrado resultados", value = "{\"message\":\"No hay eventos que coincidan con los filtros.\",\"success\":false}")
            })})
    })
    @GetMapping("/filter/{fallaId}")
    public ResponseEntity<?> filterEvents(@PathVariable @Valid Long fallaId,
                                          @ModelAttribute @Valid EventFilter filter)
    {
        List<EventCreateDto> results = eventService.findByFilters(fallaId,filter);
        if(results.isEmpty()) {
            return new ResponseEntity<>(new ApiMessageResponse("No hay eventos que coinicdan con los filtros.", false), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(results, HttpStatus.OK);

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
        if(Objects.isNull(eventService.readEvent(eventId))) {
            return new ResponseEntity<>(new ApiMessageResponse("El evento a eliminar no existe.",false), HttpStatus.NOT_FOUND);
        }
        try {
            eventService.deleteEvent(eventId, email);
            return new ResponseEntity<>(new ApiMessageResponse("Evento eliminado",true), HttpStatus.OK);
        } catch(AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.FORBIDDEN);
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
    @PutMapping("/update/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable @Valid Long eventId,
                                         @RequestBody @Valid EventUpdateDto newEvent,
                                         Authentication authentication) {
        String email = authentication.getName();
        if(Objects.isNull(eventService.readEvent(eventId))) {
            return new ResponseEntity<>(new ApiMessageResponse("El evento con id: " + eventId + " no existe.", false), HttpStatus.NOT_FOUND);
        }
        try {
            EventCreateDto result = eventService.updateEvent(newEvent, eventId, email);
            if(Objects.isNull(result)) {
                return new ResponseEntity<>(new ApiMessageResponse("El evento con id:" + eventId + " no se ha podido actualizar", false), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>("Evento actualizado.", HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.FORBIDDEN);
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
                                       @RequestParam("userId") @Valid Long userId) {
        if(Objects.nonNull(assistService.readAssist(userId, eventId))) {
            return new ResponseEntity<>(new ApiMessageResponse("La asistencia del usuario con id: " + userId + " al evento con id: " +eventId + " ya existe.",false), HttpStatus.CONFLICT);
        }
        AssistDto result = assistService.createAssist(userId, eventId);
        return new ResponseEntity<>(new ApiMessageResponse("Asistencia creada con id: " +  result.getAssistId(), true), HttpStatus.CREATED);
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
    @DeleteMapping("/leave/{eventId}")
    public ResponseEntity<?> leaveEvent(@PathVariable @Valid Long eventId,
                                        @RequestParam("userId") @Valid Long userId) {
        AssistDto assist = assistService.readAssist(userId, eventId);
        if(Objects.isNull(assist)) {
            return new ResponseEntity<>(new ApiMessageResponse("La asistencia del usuario con id: " + userId + " al evento con id: " + eventId + " no existe.", false), HttpStatus.NOT_FOUND);
        }
        assistService.deleteAssist(assist.getAssistId());
        return new ResponseEntity<>(new ApiMessageResponse("La asistencia con id: " +assist.getAssistId() + " ha sido eliminada.", true), HttpStatus.OK);
    }

    @Tags({
            @Tag(name = "Filtrado"),
            @Tag(name = "Asistencias")
    })
    @Operation(summary = "Obtiene las asistencias a un evento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Evento inexistente.", value = "El evento con id: 1 no existe.")
            })}),
            @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema = @Schema(implementation =  ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Permisos inecesarios.", value = "{\"message\":\"Sin permiso.\",\"success\":false}"),
                    @ExampleObject(name = "Falla incorrecta.", value = "{\"message\":\"Sin permiso para esta falla.\",\"success\":false}")
            })})
    })
    @GetMapping("/assists/{eventId}")
    public ResponseEntity<?> getAssists(@PathVariable @Valid Long eventId,
                                        Authentication authentication) {
        String email = authentication.getName();
        EventCreateDto event = eventService.readEvent(eventId);
        if(Objects.isNull(event)) {
            return new ResponseEntity<>(new ApiMessageResponse("El evento con id: " + eventId + " no existe.",false), HttpStatus.NOT_FOUND);
        }
        try {
            List<AssistResultDto> result = eventService.getAssists(eventId, email);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.FORBIDDEN);
        }

    }

    @Tags({
            @Tag(name = "Filtrado"),
            @Tag(name = "N. Alimentarias"),
            @Tag(name = "Eventos")
    })
    @Operation(summary = "Obtiene las necesidades alimentarias del evento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FoodNeedResultDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Evento no encontrado", value = "{\"message\":\"El evento con id: 1 no existe.\",\"success\":false}"),
                    @ExampleObject(name = "Evento sin usuarios con necesidades", value = "{\"message\":\"El evento con id: 1 no tiene asistencias.\",\"success\":false}")
            })}),
            @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema = @Schema(implementation =  ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Permisos inecesarios.", value = "{\"message\":\"Sin permiso.\",\"success\":false}"),
                    @ExampleObject(name = "Falla incorrecta.", value = "{\"message\":\"Sin permiso para esta falla.\",\"success\":false}")
            })})
    })
    @GetMapping("/foodNeed/{eventId}")
    public ResponseEntity<?> getFoodNeeds(@PathVariable @Valid Long eventId,
                                          Authentication authentication) {
        String email = authentication.getName();
        if(Objects.isNull(eventService.readEvent(eventId))) {
            return new ResponseEntity<>(new ApiMessageResponse("El evento con id: " + eventId + " no existe.", false), HttpStatus.NOT_FOUND);
        }
        try {
            List<FoodNeedResultDto> result = eventService.getFoodNeeds(eventId,email);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NullPointerException nullEx) {
            return new ResponseEntity<>(new ApiMessageResponse(nullEx.getMessage(),false),HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.FORBIDDEN);
        }
    }

    @Tags({
            @Tag(name = "Filtrado"),
            @Tag(name = "Asistencias"),
            @Tag(name = "Eventos")
    })
    @Operation(summary = "Devuelve la cantidad de gente que hay apuntada en un evento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = Integer.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Evento no encontrado", value = "{\"message\":\"El evento con id: 1 no existe.\",\"success\":false}")
            })}),
            @ApiResponse(responseCode = "403", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Permisos inecesarios.", value = "{\"message\":\"Sin permiso.\",\"success\":false}"),
                    @ExampleObject(name = "Falla incorrecta.", value = "{\"message\":\"Sin permiso para esta falla.\",\"success\":false}")

            })})
    })
    @GetMapping("/peopleCount/{eventId}")
    public ResponseEntity<?> getPeopleCount(@PathVariable @Valid Long eventId,
                                            Authentication authentication) {
        String email = authentication.getName();
        if(Objects.isNull(eventService.readEvent(eventId))) {
            return new ResponseEntity<>(new ApiMessageResponse("El evento con id: " + eventId + " no existe.", false), HttpStatus.NOT_FOUND);
        }
        try {
            int result = eventService.getPeopleCount(eventId, email);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (AccessDeniedException accEx) {
            return new ResponseEntity<>(new ApiMessageResponse(accEx.getMessage(), false), HttpStatus.FORBIDDEN);
        }

    }


}
