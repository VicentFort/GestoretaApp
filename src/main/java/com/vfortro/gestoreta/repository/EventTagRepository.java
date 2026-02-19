package com.vfortro.gestoreta.repository;

import com.vfortro.gestoreta.model.EventTag;
import com.vfortro.gestoreta.model.Falla;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTagRepository extends JpaRepository<EventTag, Long> {
    @NotNull EventTag findTagById(Long id);

    boolean existsEventTagByNameAndFalla(String name, Falla falla);
}
