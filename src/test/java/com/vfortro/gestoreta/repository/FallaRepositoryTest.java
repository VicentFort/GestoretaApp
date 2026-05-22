package com.vfortro.gestoreta.repository;

import com.vfortro.gestoreta.model.Falla;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FallaRepositoryTest {

    @Autowired
    private FallaRepository fallaRepository;

    @Test
    void existsById_ok() {

        Falla falla = new Falla();
        falla.setName("Falla Centro");

        Falla saved = fallaRepository.save(falla);

        boolean exists = fallaRepository.existsById(saved.getId());

        assertTrue(exists);
    }

    @Test
    void findById_ok() {

        Falla falla = new Falla();
        falla.setName("Falla Norte");

        Falla saved = fallaRepository.save(falla);

        assertTrue(fallaRepository.findById(saved.getId()).isPresent());
    }
}