package com.vfortro.gestoreta.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vfortro.gestoreta.service.auth.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    // Clave de 256 bits generada de forma segura codificada en Base64 para HMAC-SHA
    private final String VALID_BASE64_SECRET = Base64.getEncoder().encodeToString(
            "mi_clave_secreta_super_segura_de_32_bytes_minimo".getBytes()
    );

    @BeforeEach
    void setUp() {
        // Simulamos la inyección del @Value("${jwt.secret}") usando ReflectionTestUtils
        ReflectionTestUtils.setField(jwtService, "secretKey", VALID_BASE64_SECRET);

        // Configuración por defecto para el mock del UserDetails
        lenient().when(userDetails.getUsername()).thenReturn("testuser@falla.com");
    }

    @Test
    void generateToken_ShouldReturnValidJwtString() {
        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isBlank());
        // Un JWT válido tiene 3 partes separadas por puntos (header.payload.signature)
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void extractUsername_ShouldReturnCorrectSubject() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertEquals("testuser@falla.com", username);
    }

    @Test
    void isTokenValid_WithCorrectUserAndNotExpired_ShouldReturnTrue() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WithDifferentUser_ShouldReturnFalse() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        UserDetails anotherUser = mock(UserDetails.class);
        when(anotherUser.getUsername()).thenReturn("wronguser@falla.com");

        // Act
        boolean isValid = jwtService.isTokenValid(token, anotherUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void extractClaim_ShouldExecuteCustomClaimsResolver() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        // Extraemos un claim estándar (como el "subject") usando una función personalizada
        String subject = jwtService.extractClaim(token, claims -> claims.getSubject());

        // Assert
        assertEquals("testuser@falla.com", subject);
    }
}