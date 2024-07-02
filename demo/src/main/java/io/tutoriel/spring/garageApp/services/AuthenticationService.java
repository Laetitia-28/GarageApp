package io.tutoriel.spring.garageApp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.tutoriel.spring.garageApp.controllers.AuthenticationRequest;
import io.tutoriel.spring.garageApp.controllers.AuthenticationResponse;
import io.tutoriel.spring.garageApp.controllers.RegisterRequest;
import io.tutoriel.spring.garageApp.controllers.changePasswordRequest;
import io.tutoriel.spring.garageApp.models.Token;
import io.tutoriel.spring.garageApp.models.TokenTYpe;
import io.tutoriel.spring.garageApp.models.User;
import io.tutoriel.spring.garageApp.repositories.TokenRepository;
import io.tutoriel.spring.garageApp.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository; //b/c we need to interact with the DB. 'final' to ensure it's injected and remove the private access modifier

    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    private  final  JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) { //Register method will allow to create a user, save it to the DB and return the generated token out of it
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);
        var refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                //token(jwtToken)
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        // before saving the user token, we must revoke all the previous one
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken); // save token in db
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                //.token(jwtToken)
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenTYpe.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final  String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final  String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) { // check if the JWT Token exists. It's usually preceded by the word 'Bearer'
            return;
        }
        refreshToken = authHeader.substring(7); //To extract the token from authHeader. beginIndex=7 b/c "bearer"=6 characters
        userEmail = jwtService.extractUsername(refreshToken); //todo extract the userEmail from token
        if (userEmail != null ) { // ensure that the user's email exist
            var userDetails = this.userRepository.findByEmail(userEmail).orElseThrow(); // to check if we have the user within the database
            if(jwtService.isTokenValid(refreshToken, userDetails)){
               var accessToken = jwtService.generateToken(userDetails);
//               revokeAllUserTokens(userDetails);
//               saveUserToken(userDetails, accessToken);
               var authResponse = AuthenticationResponse.builder()
                       .accessToken(accessToken)
                       .refreshToken(refreshToken)
                       .build();
               new ObjectMapper().writeValue(response.getOutputStream(), authResponse); // this will be the body of the response
            }
        }
    }

}
