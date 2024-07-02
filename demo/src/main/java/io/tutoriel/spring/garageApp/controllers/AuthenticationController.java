package io.tutoriel.spring.garageApp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.tutoriel.spring.garageApp.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor // automatically generates a constructor that initializes all the final fields in the class at compile time, not at the time of field declaration.
public class AuthenticationController {

    private final AuthenticationService service;

    @Operation(summary = "Endpoint to register a new user")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @Operation(summary = "Endpoint to authenticate an existing user")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @Operation(summary = "Endpoint to refresh the token")
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request, // object where we can get/read the authorization header which will hold the every refresh token
            HttpServletResponse response // object that will help us to reinject or sent back the response to the user
    ) throws IOException {
        service.refreshToken(request, response);
    }

}
