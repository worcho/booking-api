package io.github.dvirisha.booking_api;

import io.github.dvirisha.booking_api.auth.AuthService;
import io.github.dvirisha.booking_api.auth.dto.CreateUserRequest;
import io.github.dvirisha.booking_api.common.error.ConflictException;
import io.github.dvirisha.booking_api.user.Role;
import io.github.dvirisha.booking_api.user.User;
import io.github.dvirisha.booking_api.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.CapturesArguments;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("create()")
    class create {

        @Test
        void shouldCreateUserSuccessfully() {
            CreateUserRequest request = new CreateUserRequest("TestUser", "TestPassword");

            when(userRepository.existsByUsername(request.username()))
                    .thenReturn(false);
            when(passwordEncoder.encode(request.password()))
                    .thenReturn("EncodedPassword");

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

            authService.createUser(request);

            verify(userRepository).save(captor.capture());
            User captorValue = captor.getValue();
            assertAll(
                    () -> assertEquals(request.username(), captorValue.getUsername()),
                    () -> assertEquals("EncodedPassword", captorValue.getPassword()),
                    () -> assertEquals(Role.USER, captorValue.getRole())
            );
        }

        @Test
        void shouldThrowConflictExceptionWhenUserExists() {
            CreateUserRequest request = new CreateUserRequest("TestUser", "TestPassword");

            when(userRepository.existsByUsername(request.username()))
                    .thenReturn(true);

            assertThrowsExactly(ConflictException.class,
                    () -> authService.createUser(request));
        }
    }
}
