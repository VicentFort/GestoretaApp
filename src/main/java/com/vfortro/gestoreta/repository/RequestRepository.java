package com.vfortro.gestoreta.repository;

import com.vfortro.gestoreta.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Request findRequestById(Long id);
}
