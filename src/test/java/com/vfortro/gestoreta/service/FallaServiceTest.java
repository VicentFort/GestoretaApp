package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.FallaConversor;
import com.vfortro.gestoreta.dto.fallas.FallaCreateDTO;
import com.vfortro.gestoreta.dto.fallas.info.FallaUserInfoDTO;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.repository.FallaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FallaServiceTest {

    @Mock
    private FallaRepository fallaRepository;

    @Mock
    private FallaConversor fallaConversor;

    @InjectMocks
    private FallaService fallaService;

    @Test
    void readFalla_ok() {

        Falla falla = new Falla();
        FallaCreateDTO dto = new FallaCreateDTO();

        when(fallaRepository.findById(anyLong()))
                .thenReturn(Optional.of(falla));

        when(fallaConversor.fromEntity2DTO(any()))
                .thenReturn(dto);

        FallaUserInfoDTO result = fallaService.readFalla(1L);

        assertNotNull(result);
    }

    @Test
    void readFalla_notFound() {

        when(fallaRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> fallaService.readFalla(1L));
    }
}