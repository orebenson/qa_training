package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Garage garage = new Garage();

        // Add vehicles
        Vehicle car1 = new Car("Polo", "Diesel");
        Vehicle bike1 = new Motorbike("BMW Bike", "Petrol");
        Long car1_ID = garage.addVehicle(car1);
        Long bike1_ID = garage.addVehicle(bike1);

        // Fix a vehicle
        double car1_bill = (double) garage.fixVehicle(car1_ID);
        System.out.println("Bill for fixing car 1: " + car1_bill);

        double bike1_bill = (double) garage.fixVehicle(bike1_ID);
        System.out.println("Bill for fixing bike 1: " + bike1_bill);

        // Remove a vehicle
        Vehicle removedVehicle = garage.removeVehicle(car1_ID);
        System.out.println("Removed vehicle: " + removedVehicle.getName());

        // Remove vehicles by type
        List<Vehicle> removedCars = garage.removeVehiclesByType("Car");
        System.out.println("Removed cars: " + removedCars);

        // Empty the garage
        garage.emptyGarage();
        System.out.println("Garage emptied. Total vehicles: " + garage.getTotalVehicles());
    }
}
