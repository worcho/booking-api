package io.github.dvirisha.booking_api.auth;

import io.github.dvirisha.booking_api.common.error.ConflictException;
import io.github.dvirisha.booking_api.user.Role;
import io.github.dvirisha.booking_api.user.User;
import io.github.dvirisha.booking_api.user.UserRepository;
import io.github.dvirisha.booking_api.auth.dto.CreateUserRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already exists");
        }

        userRepository.save(new User(request.username(), passwordEncoder.encode(request.password()), Role.USER));
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnsupportedOperationException("You are not logged in or principal cannot be found.");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User user)) {
            throw new RuntimeException("Invalid principal");
        }

        return user;
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
