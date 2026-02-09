package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.EventCreateDto;
import com.vfortro.gestoreta.dto.EventFilter;
import com.vfortro.gestoreta.dto.EventUpdateDto;
import com.vfortro.gestoreta.service.EventService;
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
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Evento no encontrado", value = "Evento no encontrado")
            })})
    })
    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable @Valid Long eventId) {
        EventCreateDto eventCreateDto = eventService.readEvent(eventId);
        if(!Objects.nonNull(eventCreateDto)) {
            System.out.println("Evento con id: " + eventId + " no encontrado.");
            return new ResponseEntity<>("Evento no encontrado", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(eventCreateDto,HttpStatus.OK);
    }


    @Operation(summary = "Crea un evento en la base de datos.", description = "Falla en caso de que exista un evento con la misma Id.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Evento creado", value = "Evento creado")
            })}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Falla inexistente", value = "La falla a la que se está asociando el evento: GRAN EVENTO no existe"),
                    @ExampleObject(name = "Etiqueta de evento inexistente", value = "La etiqueta a la que se está asociando el evento: GRAN EVENTO no existe"),
            })})
    })
    @PostMapping("/create")
    public ResponseEntity<?> postEvent(@Valid @RequestBody EventCreateDto event) {
        try {
            eventService.createEvent(event);
        } catch(EntityNotFoundException entityEx) {
            return new ResponseEntity<>(entityEx.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Evento creado", HttpStatus.CREATED);
    }

    @Operation(summary = "Filtra los eventos de una falla")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventCreateDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "No se han encontrado resultados", value = "No hay eventos que coincidan con los filtros")
            })})
    })
    @GetMapping("/filter/{fallaId}")
    public ResponseEntity<?> filterEvents(@PathVariable @Valid Long fallaId,
                                          @ModelAttribute @Valid EventFilter filter) {
        List<EventCreateDto> results = eventService.findByFilters(fallaId,filter);
        if(results.isEmpty()) {
            return new ResponseEntity<>("No hay eventos que coinicdan con los filtros.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(results, HttpStatus.OK);

    }

    @Operation(summary = "Elimina un evento de la base de datos dada su id.")
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable @Valid Long eventId) {
        if(Objects.isNull(eventService.readEvent(eventId))) {
            return new ResponseEntity<>("El evento a eliminar no existe", HttpStatus.NOT_FOUND);
        }
        eventService.deleteEvent(eventId);
        return new ResponseEntity<>("Evento eliminado", HttpStatus.OK);
    }

    @Operation(summary = "Actualiza un evento en la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Evento no encontrado", value = "El evento con id: 1 no existe.")
            })}),
            @ApiResponse(responseCode = "500", content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Error al actualizar", value = "El evento con id: 1 no se ha podido actualizar.")
            })})
    })
    @PutMapping("/update/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable @Valid Long eventId,
                                         @RequestBody @Valid EventUpdateDto newEvent) {
        if(Objects.isNull(eventService.readEvent(eventId))) {
            return new ResponseEntity<>("El evento con id: " + eventId + " no existe.", HttpStatus.NOT_FOUND);
        }
        EventCreateDto result = eventService.updateEvent(newEvent, eventId);
        if(Objects.isNull(result)) {
            return new ResponseEntity<>("El evento con id:" + eventId + " no se ha podido actualizar", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Evento actualizado.", HttpStatus.OK);
    }
}
