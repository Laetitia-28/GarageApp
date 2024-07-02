package io.tutoriel.spring.garageApp;

import Role.Role;
import io.tutoriel.spring.garageApp.controllers.RegisterRequest;
import io.tutoriel.spring.garageApp.services.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static Role.Role.ADMIN;
import static Role.Role.MANAGER;

@SpringBootApplication
public class GarageAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(GarageAppApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner (AuthenticationService service){
		return args -> {
			var admin = RegisterRequest.builder()
					.firstname("Admin")
					.lastname("Admin")
					.email("admin@mail.com")
					.password("password")
					.role(ADMIN)
					.build();
			System.out.println("Admin token : " + service.register(admin).getAccessToken());

			var manager = RegisterRequest.builder()
					.firstname("Manager")
					.lastname("Manager")
					.email("manager@mail.com")
					.password("password")
					.role(MANAGER)
					.build();
			System.out.println("Manager token : " + service.register(manager).getAccessToken());
		};

	};

}
