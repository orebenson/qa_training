package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Garage {
    ArrayList<Vehicle> vehicles;

    public Garage() {
        vehicles = new ArrayList<>();
    }

    public Long addVehicle(Vehicle vehicle) {
        vehicle.setId((long) (this.vehicles.size() + 1));
        this.vehicles.add(vehicle);
        return vehicle.getId();
    }
    
    public Vehicle removeVehicle(Long id) {
        if (id > vehicles.size()) return null;
        for (Vehicle vehicle : vehicles) {
            if (Objects.equals(vehicle.getId(), id)) {
                vehicles.remove(vehicle);
                return vehicle;
            }
        }
        return null;
    }

    public List<Vehicle> removeVehiclesByType(String type) {
        List<Vehicle> vehiclesReturn = new ArrayList<>();
        if (Objects.equals(type, "Car")) {
            for (Vehicle vehicle : this.vehicles) {
                if (vehicle instanceof Car) {
                    this.vehicles.remove(vehicle);
                    vehiclesReturn.add(vehicle);
                }
            }
        }
        if (Objects.equals(type, "Motorbike")) {
            for (Vehicle vehicle : this.vehicles) {
                if (vehicle instanceof Motorbike) {
                    this.vehicles.remove(vehicle);
                    vehiclesReturn.add(vehicle);
                }
            }
        }
        return vehiclesReturn;
    }

    private double calculateBill(Vehicle vehicle) {
        if (vehicle instanceof Car) return 500;
        if (vehicle instanceof Motorbike) return 300;
        return 200;
    }

    public Object fixVehicle(long id) {
        if (id > vehicles.size()) return null;
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId() == id) {
                return calculateBill(vehicle);
            }
        }
        return null;
    }

    public int getTotalVehicles() {
        return this.vehicles.size();
    }

    public void emptyGarage() {
        this.vehicles = new ArrayList<>();
    }
}
