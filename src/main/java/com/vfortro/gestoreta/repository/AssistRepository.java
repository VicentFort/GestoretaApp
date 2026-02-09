package com.vfortro.gestoreta.repository;

import com.vfortro.gestoreta.model.Assist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssistRepository extends JpaRepository<Assist,Long> {

    Optional<Assist> findByUserIdAndEventId(Long userId, Long eventId);
}
