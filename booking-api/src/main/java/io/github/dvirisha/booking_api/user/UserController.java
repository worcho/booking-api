package io.github.dvirisha.booking_api.user;

import io.github.dvirisha.booking_api.auth.dto.CreateUserRequest;
import io.github.dvirisha.booking_api.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/user")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public void create(@Valid @RequestBody CreateUserRequest request) {
        authService.createUser(request);
    }
}
