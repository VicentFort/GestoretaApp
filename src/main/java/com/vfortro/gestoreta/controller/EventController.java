package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.ApiMessageResponse;
import com.vfortro.gestoreta.dto.EventCreateDto;
import com.vfortro.gestoreta.dto.EventFilter;
import com.vfortro.gestoreta.dto.EventUpdateDto;
import com.vfortro.gestoreta.service.EventService;
import io.swagger.annotations.Example;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @Operation(summary = "Busca un evento en la base de datos dada su Id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventCreateDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Evento no encontrado", value = "{\"message\":\"Evento no encontrado\",\"success\":false}"  )
            })})
    })
    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable @Valid Long eventId) {
        EventCreateDto eventCreateDto = eventService.readEvent(eventId);
        if(!Objects.nonNull(eventCreateDto)) {
            return new ResponseEntity<>(new ApiMessageResponse("Evento no encontrado", false), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(eventCreateDto,HttpStatus.OK);
    }


    @Operation(summary = "Crea un evento en la base de datos.", description = "Falla en caso de que exista un evento con la misma Id.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Evento creado", value = "{\"message\":\"Evento creado\",\"success\":true}")
            })}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Falla inexistente", value = "{\"message\":\"La falla a la que se está asociando el evento: GRAN EVENTO no existe\",\"success\":false}"),
                    @ExampleObject(name = "Etiqueta de evento inexistente", value = "{\"message\":\"La etiqueta a la que se está asociando el evento: GRAN EVENTO no existe\",\"success\":false}"),
            })})
    })
    @PostMapping("/create")
    public ResponseEntity<?> postEvent(@Valid @RequestBody EventCreateDto event) {
        try {
            eventService.createEvent(event);
        } catch(EntityNotFoundException entityEx) {
            return new ResponseEntity<>(new ApiMessageResponse(entityEx.getMessage(), false), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiMessageResponse("Evento creado.", true), HttpStatus.CREATED);
    }

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

    @Operation(summary = "Elimina un evento de la base de datos dada su id.")
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable @Valid Long eventId) {
        if(Objects.isNull(eventService.readEvent(eventId))) {
            return new ResponseEntity<>(new ApiMessageResponse("El evento a eliminar no existe",false), HttpStatus.NOT_FOUND);
        }
        eventService.deleteEvent(eventId);
        return new ResponseEntity<>(new ApiMessageResponse("Evento eliminado",true), HttpStatus.OK);
    }

    @Operation(summary = "Actualiza un evento en la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Evento no encontrado", value = "{\"message\":\"El evento con id: 1 no existe.\",\"success\":false}")
            })}),
            @ApiResponse(responseCode = "500", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessageResponse.class), examples = {
                    @ExampleObject(name = "Error al actualizar", value = "{\"message\":\"El evento con id: 1 no se ha podido actualizar.\",\"success\":false}")
            })})
    })
    @PutMapping("/update/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable @Valid Long eventId,
                                         @RequestBody @Valid EventUpdateDto newEvent) {
        if(Objects.isNull(eventService.readEvent(eventId))) {
            return new ResponseEntity<>(new ApiMessageResponse("El evento con id: " + eventId + " no existe.", false), HttpStatus.NOT_FOUND);
        }
        EventCreateDto result = eventService.updateEvent(newEvent, eventId);
        if(Objects.isNull(result)) {
            return new ResponseEntity<>(new ApiMessageResponse("El evento con id:" + eventId + " no se ha podido actualizar", false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Evento actualizado.", HttpStatus.OK);
    }
}
