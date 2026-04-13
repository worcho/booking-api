package io.github.dvirisha.booking_api;

import io.github.dvirisha.booking_api.auth.JwtService;
import io.github.dvirisha.booking_api.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "test-secret-key-that-is-long-enough-32chars", 60);
    }

    @Test
    void shouldExtractUsernameCorrectly() {
        String token = jwtService.generateToken("TestUsername");

        String username = jwtService.extractUsername(token);

        assertEquals("TestUsername", username);
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        User user = new User();
        user.setUsername("TestUsername");
        String token = jwtService.generateToken(user.getUsername());

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void shouldBeInvalidWhenUsernameIsIncorrect() {
        User user = new User();
        user.setUsername("TestUsername");
        String token = jwtService.generateToken("IncorrectUsername");

        assertFalse(jwtService.isTokenValid(token, user));
    }

    @Test
    void shouldBeInvalidWhenTokenIsExpired() throws InterruptedException {
        JwtService shortLivedService = new JwtService(
                "test-secret-key-that-is-long-enough-32chars", 0);

        User user = new User();
        user.setUsername("TestUsername");
        String token = shortLivedService.generateToken(user.getUsername());

        Thread.sleep(10);

        assertFalse(shortLivedService.isTokenValid(token, user));
    }

    @Test
    void shouldBeExpired() throws InterruptedException {
        JwtService shortLivedService = new JwtService(
                "test-secret-key-that-is-long-enough-32chars", 0);
        String expiredToken = shortLivedService.generateToken("TestUsername");

        Thread.sleep(10);

        assertTrue(jwtService.isExpired(expiredToken));
    }
}
