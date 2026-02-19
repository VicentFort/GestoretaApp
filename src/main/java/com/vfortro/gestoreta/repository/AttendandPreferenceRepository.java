package com.vfortro.gestoreta.repository;

import com.vfortro.gestoreta.model.AttendantPreference;
import com.vfortro.gestoreta.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendandPreferenceRepository extends JpaRepository<AttendantPreference, Long> {
    AttendantPreference getAttendantPreferenceById(Long id);

    List<AttendantPreference> getAllById(Long id);

    List<AttendantPreference> getAllByUser(User user);
}
