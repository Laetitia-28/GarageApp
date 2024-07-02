package io.tutoriel.spring.garageApp.config;


import io.tutoriel.spring.garageApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    public final UserRepository repository;

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> repository.findByEmail(username) // we want to fetch the user from the database
                .orElseThrow(() -> new UsernameNotFoundException("User not found")); // in case we don't find a user un the DB, we'll return this exception
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // authenticationProvider is the data access object which is responsible to fetch the user details and also include password and all others
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        //Specification of 2 properties
        authProvider.setUserDetailsService(userDetailsService()); // tell the auth Provider which user details service to use in order to fetch information about our user b/c we might have multiple implementations of the userDetails
        authProvider.setPasswordEncoder(passwordEncoder()); // password encoder we are using within our application in order to decode it later using the correct algorithm
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //The auth manager is the one responsible to manage the auth
    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
