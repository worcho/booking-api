package io.github.dvirisha.booking_api.auth;

import io.github.dvirisha.booking_api.auth.dto.GenerateTokenRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/authenticate")
    public String generateToken(@RequestBody GenerateTokenRequest generateTokenRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(generateTokenRequest.username(), generateTokenRequest.password()));
        return jwtService.generateToken(authenticate.getName());
    }
}
