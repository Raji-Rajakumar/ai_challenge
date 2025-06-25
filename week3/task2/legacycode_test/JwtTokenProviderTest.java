package com.busbooking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private static final String TEST_SECRET = Base64.getEncoder().encodeToString(
        Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded()
    );
    private static final int TEST_EXPIRATION = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        jwtTokenProvider.setJwtSecret(TEST_SECRET);
        jwtTokenProvider.setJwtExpirationInMs(TEST_EXPIRATION);
    }

    @Test
    void generateToken_Success() {
        // Arrange
        String email = "test@example.com";

        // Act
        String token = jwtTokenProvider.generateToken(email);

        // Assert
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // Valid JWT has 3 parts

        // Verify token contents
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Base64.getDecoder().decode(TEST_SECRET))
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(email, claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void getEmailFromJWT_Success() {
        // Arrange
        String email = "test@example.com";
        String token = jwtTokenProvider.generateToken(email);

        // Act
        String extractedEmail = jwtTokenProvider.getEmailFromJWT(token);

        // Assert
        assertEquals(email, extractedEmail);
    }

    @Test
    void validateToken_ValidToken() {
        // Arrange
        String email = "test@example.com";
        String token = jwtTokenProvider.generateToken(email);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_ExpiredToken() {
        // Arrange
        String email = "test@example.com";
        jwtTokenProvider.setJwtExpirationInMs(-1000); // Set to expired
        String token = jwtTokenProvider.generateToken(email);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertFalse(isValid);
    }
} 