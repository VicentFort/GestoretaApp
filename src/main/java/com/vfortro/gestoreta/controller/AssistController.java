package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.ApiMessageResponse;
import com.vfortro.gestoreta.dto.AssistDto;
import com.vfortro.gestoreta.service.AssistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/assist")
public class AssistController {
    @Autowired
    private AssistService assistService;

    @PostMapping("/create")
    public ResponseEntity<?> createAssist(@RequestBody @Valid AssistDto assist) {
        if(Objects.nonNull(assistService.readAssist(assist.getUserId(), assist.getEventId()))) {
            return new ResponseEntity<>(new ApiMessageResponse("La asistencia al evento ya existe",false), HttpStatus.CONFLICT);
        }
        assistService.createAssist(assist);
        return new ResponseEntity<>(new ApiMessageResponse("Asistencia creada", true), HttpStatus.OK);
    }
}
