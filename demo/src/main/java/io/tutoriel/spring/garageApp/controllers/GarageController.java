package io.tutoriel.spring.garageApp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.tutoriel.spring.garageApp.models.Car;
import io.tutoriel.spring.garageApp.services.GarageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(description = "Endpoints to manage File", name = "File Rest Controller")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/cars")
public class GarageController {

    @Autowired
    private GarageService garageService;

    @Operation(summary = "Endpoint to list all the cars")
    @GetMapping("/car") // lorsqu'on veut lire les infos de la BD
    public List<Car> getAll() {
        return garageService.getAll();
    }

    @Operation(summary = "Endpoint to create a car")
    @PostMapping("/car")
    public  Car create(@RequestBody Car car){
        return garageService.create(car);
    }

    @Operation(summary = "Endpoint to delete a car")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id){
        garageService.delete(id);
    }

    @Operation(summary = "Endpoint to delete several cars at once")
    @DeleteMapping("/deleteAll")
    public void deleteAll(@RequestParam("ids") List <Long> ids){garageService.deleteAll(ids);}

    @Operation(summary = "Endpoint to get a car by it's id")
    @GetMapping("/cars")
    public Car getCar(long id){
        return  garageService.getById(id);
    }

    @Operation(summary = "Endpoint to update any car")
    @PutMapping("/car/{id}")
    public  Car update(@RequestBody Car car, @PathVariable long id){
        return garageService.update(car, id);
    }
}
