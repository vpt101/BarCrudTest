package com.brclys.thct.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_SECRET = "testSecretKey123456789012345678901234567890";
    private static final int TEST_EXPIRATION_MS = 86400000; // 24 hours

    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // Set up test values using reflection
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", TEST_EXPIRATION_MS);
        
        // Manually initialize the key
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(jwtUtil, "key", key);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String token = jwtUtil.generateToken(TEST_USERNAME);
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(TEST_SECRET.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
                
        assertEquals(TEST_USERNAME, claims.getSubject());
    }

    @Test
    void getUsernameFromToken_ShouldReturnUsername() {
        String token = jwtUtil.generateToken(TEST_USERNAME);
        String username = jwtUtil.getUsernameFromToken(token);
        assertEquals(TEST_USERNAME, username);
    }

    @Test
    void validateJwtToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = jwtUtil.generateToken(TEST_USERNAME);
        
        // Act
        boolean isValid = jwtUtil.validateJwtToken(token);
        
        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateJwtToken_WithExpiredToken_ShouldReturnFalse() {
        String expiredToken = Jwts.builder()
                .setSubject(TEST_USERNAME)
                .setIssuedAt(new Date(System.currentTimeMillis() - TEST_EXPIRATION_MS - 1000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
        
        // Act
        boolean isValid = jwtUtil.validateJwtToken(expiredToken);
        
        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_WithInvalidToken_ShouldReturnFalse() {
        // Arrange
        String invalidToken = "invalid.token.string";
        
        // Act
        boolean isValid = jwtUtil.validateJwtToken(invalidToken);
        
        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_WithMalformedToken_ShouldReturnFalse() {
        String malformedToken = "malformed.token";
        boolean isValid = jwtUtil.validateJwtToken(malformedToken);
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_WithEmptyToken_ShouldReturnFalse() {
        String emptyToken = "";
        boolean isValid = jwtUtil.validateJwtToken(emptyToken);
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_WithNullToken_ShouldReturnFalse() {
        boolean isValid = jwtUtil.validateJwtToken(null);
        assertFalse(isValid);
    }

    @Test
    void generateToken_WithNullUsername_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.generateToken(null);
        });
    }

    @Test
    void getUsernameFromToken_WithNullToken_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.getUsernameFromToken(null);
        });
    }
}
