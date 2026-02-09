package com.vfortro.gestoreta.repository;

import com.vfortro.gestoreta.model.Event;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventRepository extends JpaRepository<Event,Long>, JpaSpecificationExecutor<Event> {

    Event findEventByTitle(String title);

    @NotNull Event findEventById(Long id);
}
