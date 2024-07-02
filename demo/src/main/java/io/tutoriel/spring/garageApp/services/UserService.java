package io.tutoriel.spring.garageApp.services;

import io.tutoriel.spring.garageApp.controllers.changePasswordRequest;
import io.tutoriel.spring.garageApp.models.User;
import io.tutoriel.spring.garageApp.repositories.TokenRepository;
import io.tutoriel.spring.garageApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    public void changePassword(changePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        //check if the correct password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
            throw new IllegalStateException("Wrong password");
        }

        //check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())){
            throw new IllegalStateException("Password are not the same");
        }

        //update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        //save the new password
        userRepository.save(user);
    }

}
