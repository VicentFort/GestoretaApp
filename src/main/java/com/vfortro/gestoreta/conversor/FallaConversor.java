package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.fallas.FallaCreateDto;
import com.vfortro.gestoreta.model.Falla;
import org.springframework.stereotype.Service;

@Service
public class FallaConversor {
    public Falla fromDto2Entity(FallaCreateDto dto) {
        Falla falla = new Falla();
        falla.setId(dto.getFallaId());
        falla.setName(dto.getName());
        falla.setCreationDate(dto.getCreationDate());
        falla.setShieldUrl(dto.getShieldUrl());
        return falla;
    }
    public FallaCreateDto fromEntity2DTO(Falla falla) {
        FallaCreateDto dto = new FallaCreateDto();
        dto.setFallaId(falla.getId());
        dto.setName(falla.getName());
        dto.setCreationDate(falla.getCreationDate());
        dto.setShieldUrl(falla.getShieldUrl());
        return dto;
    }
}
