package io.tutoriel.spring.garageApp.services;

import io.tutoriel.spring.garageApp.models.Car;
import io.tutoriel.spring.garageApp.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GarageService {
    @Autowired
    private CarRepository carRepository;
    public List<Car> getAll(){
        return carRepository.findAll();
        //    return carRepository.findAll();
    }
    public Car create(Car car){
        return carRepository.save(car);
    }
    public void delete (long id){
        carRepository.deleteById(id);
    }

    public void deleteAll (List<Long> ids) { carRepository.deleteAllById(ids);}

    public Car getById(long id){
        Car car = carRepository.findById(id).orElse( null);
        return car;
    }

    public Car update(Car car, long id){
        Car carBD = carRepository.findById(id).orElse( null);
        carBD.setBrand(car.getBrand());
        carBD.setModel(car.getModel());
        carBD.setColor(car.getColor());

        return carRepository.save(carBD);
    }
}