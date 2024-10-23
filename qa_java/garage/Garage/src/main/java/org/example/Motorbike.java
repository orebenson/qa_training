package org.example;

public class Motorbike implements Vehicle {
    long id;
    int numOfWheels;
    String fuelType;
    int tank;
    String name;

    public Motorbike(String name, String fuelType) {
        this.id = 0L;
        this.numOfWheels = 2;
        this.fuelType = fuelType;
        this.tank = 0;
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getFuelType() {
        return this.fuelType;
    }

    public void setNumOfWheels(int numOfWheels) {
        this.numOfWheels = numOfWheels;
    }

    public int getNumOfWheels() {
        return this.numOfWheels;
    }

    public void fillTank() {
        this.tank = 10;
    }

    public void drive() {
        this.tank -= 1;
    }
}
