package io.tutoriel.spring.garageApp.repositories;
import io.tutoriel.spring.garageApp.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
