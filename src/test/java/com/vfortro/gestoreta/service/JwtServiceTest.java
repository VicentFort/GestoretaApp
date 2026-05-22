package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.service.auth.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void generateToken_ok() {

        UserDetails user = User
                .withUsername("vicent@email.com")
                .password("1234")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
    }

    @Test
    void extractUsername_ok() {

        UserDetails user = User
                .withUsername("vicent@email.com")
                .password("1234")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(user);

        String username = jwtService.extractUsername(token);

        assertEquals("vicent@email.com", username);
    }
}