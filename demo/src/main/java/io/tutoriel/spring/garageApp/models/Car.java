package io.tutoriel.spring.garageApp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //générer à chaque fois des id nouveaux à la création d'utilisateurs
    private long id;
    private String model;
    private String brand;
    private Color color;

    private enum  Color {
        RED,
        GREEN,
        YELLOW,
        BLACK
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Car(long id, String model, String brand, Color color) {
        this.id = id;
        this.model = model;
        this.brand = brand;
        this.color = color;
    }
    public Car() {


    }


}
