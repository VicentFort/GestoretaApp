package com.vfortro.gestoreta.repository;

import com.vfortro.gestoreta.model.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserById(Long userId);

    Optional<User> findByEmail(String email);

    User findUserByEmail(String email);

    boolean existsByEmail(String email);
}
