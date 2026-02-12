package com.vfortro.gestoreta.repository;

import com.vfortro.gestoreta.model.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @NotNull User findUserById(Long id);

    Optional<Object> findByEmail(String email);

    @NotNull User findUserByEmail(String email);
}
