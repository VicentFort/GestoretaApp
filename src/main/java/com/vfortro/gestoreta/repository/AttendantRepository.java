package com.vfortro.gestoreta.repository;

import com.vfortro.gestoreta.model.Attendant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AttendantRepository extends JpaRepository<Attendant, Long> {
   Boolean existsByUser_IdAndEvent_Id(Long id, Long eventId);

    void deleteAllByEvent_Id(Long eventId);
}
