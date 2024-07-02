package io.tutoriel.spring.garageApp.config;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static Role.Permission.*;
import static Role.Role.ADMIN;
import static Role.Role.MANAGER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private static final String[] WHITE_LIST_URL = {"/auth/**", "/swagger-ui/**", "/v3/api-docs/**"};
    /*"/swagger-ui/**" refers to the Swagger UI interface, which is responsible for rendering the user interface for interacting with the API documentation.
    "/v3/api-docs/**" refers to the API documentation itself, which is typically provided in the OpenAPI (formerly known as Swagger) format.*/

    private final JwtAuthenticationFilter jwtAuthFilter;

    private final AuthenticationProvider authenticationProvider;

    private final LogoutHandler logoutHandler;

    //At the app start, spring security will try to look a bean of type Security filter chain which is the bean responsible for configuring all the http security of the app
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //http configuration
        http
                .csrf(AbstractHttpConfigurer::disable) //to disable the csrf check.
                .authorizeHttpRequests(req -> // Whitelist : endpoints that do not need/require any auth/token. E.g : Signup page and login page
                    req.requestMatchers(WHITE_LIST_URL) // list of app patterns which can be accessed without having to provide any authentication credentials or JWT tokens will be parsed here
                        .permitAll()

                    .requestMatchers("/management/**").hasAnyRole(ADMIN.name(), MANAGER.name())
                        .requestMatchers(HttpMethod.GET,"/management/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                        .requestMatchers(HttpMethod.POST,"/management/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                        .requestMatchers(HttpMethod.PUT,"/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                        .requestMatchers(HttpMethod.DELETE,"/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())

//                    .requestMatchers("/admin/**").hasRole(ADMIN.name())
//                        .requestMatchers(HttpMethod.GET,"/admin/**").hasAuthority(ADMIN_READ.name())
//                        .requestMatchers(HttpMethod.POST,"/admin/**").hasAuthority(ADMIN_CREATE.name())
//                        .requestMatchers(HttpMethod.PUT,"/admin/**").hasAuthority(ADMIN_UPDATE.name())
//                        .requestMatchers(HttpMethod.DELETE,"/admin/**").hasAuthority(ADMIN_DELETE.name())

                    .anyRequest()
                    .authenticated()
                )
                // Explanation of sessionManagement. When implementing the filter, we want every request to be authenticated i.e. the authentication/session state should not be stored. it should be stateless and therefor help us ensure that each request should be auth
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //auth provider :
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // so the jwtAuthFilter will be executed before the UsernamePasswordAuthenticationFilter (see implementation of JwtAuthenticationFilter to understand why)
                .logout()
                .logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(
                        (request, response, authentication) ->
                                SecurityContextHolder.clearContext()
                ) // this is what will happen once the logout success : the security context will be cleared so the user can't access again the API with his expired/revoked token
        ;

        return http.build(); // the build of http might throw an exception, that's the reason of adding the exception to the method signature
    }
}
