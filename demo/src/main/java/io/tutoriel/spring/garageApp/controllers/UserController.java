package io.tutoriel.spring.garageApp.controllers;

import io.tutoriel.spring.garageApp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody changePasswordRequest request,
            Principal connectedUser
    ){
        service.changePassword(request, connectedUser);
        return ResponseEntity.accepted().build();
    }

}
