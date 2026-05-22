package com.vfortro.gestoreta.repository;

import com.vfortro.gestoreta.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {

        User user = new User();
        user.setEmail("test@test.com");

        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("test@test.com");

        assertTrue(exists);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {

        User user = new User();
        user.setEmail("test@test.com");

        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("test@test.com");

        assertTrue(result.isPresent());
        assertEquals("test@test.com", result.get().getEmail());
    }
}