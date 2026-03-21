package com.vfortro.gestoreta.repository;

import com.vfortro.gestoreta.model.Event;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface EventRepository extends JpaRepository<Event,Long>, JpaSpecificationExecutor<Event> {

    Event findEventByTitle(String title);

    @NotNull Event findEventById(Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE events SET done = true, open = false " +
            "WHERE (end_date + end_hour) < CURRENT_TIMESTAMP " +
            "AND done = false",
            nativeQuery = true)
    void closeEndedEvents();
}
