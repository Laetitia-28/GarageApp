package io.tutoriel.spring.garageApp.config;

import io.tutoriel.spring.garageApp.repositories.TokenRepository;
import io.tutoriel.spring.garageApp.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor //used to create a constructor using any final field declared in the class
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private  final UserDetailsService userDetailsService;

    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    )throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if(authHeader == null || !authHeader.startsWith("Bearer ")){ // check if the JWT Token exists. It's usually preceded by the word 'Bearer'
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7); //To extract the token from authHeader. beginIndex=7 b/c "bearer"=6 characters
        userEmail = jwtService.extractUsername(jwt); //todo extract the userEmail from JWT token

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) { // ensure that the user is not authenticated and it's email is not null
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail); // to check if we have the user within the database

            var isTokenValid = tokenRepository.findByToken(jwt)
                    .map(token -> !token.isExpired() && !token.isRevoked())
                    .orElse(false);

            if(jwtService.isTokenValid(jwt, userDetails)){
                //if the token is valid we'll update the security  context and send the request to the dispatcher servlet
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken( //this objet is needed by spring in order to update the security context
                        userDetails,
                        null, // we don't have credentials. That's why it is parsed as null
                        userDetails.getAuthorities()
                );
                authToken.setDetails( // in order to give some more details to the authToken object recently created
                        new WebAuthenticationDetailsSource().buildDetails(request)// the details are being built out of the http request
                );
                SecurityContextHolder.getContext().setAuthentication(authToken); //Now the final step : update de security holder context
            }
        }
        filterChain.doFilter(request, response); // Always call this function in order to pass the hand to the next filters to be executed
    }
}
