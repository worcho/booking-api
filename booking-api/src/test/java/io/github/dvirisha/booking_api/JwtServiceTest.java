package io.github.dvirisha.booking_api;

import io.github.dvirisha.booking_api.auth.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // реален обект, без mock-ове
        // secret трябва да е поне 32 символа за HS256
        jwtService = new JwtService(
                "test-secret-key-that-is-long-enough-32chars", 60);
    }

    @Test
    void checkIfUsernameExtractedCorrect() {
        String token = jwtService.generateToken("TestUsername");

        String username = jwtService.extractUsername(token);

        assertEquals("TestUsername", username);
    }

    @Test
    void checkIfTokenIsNotExpired() {

    }
}
