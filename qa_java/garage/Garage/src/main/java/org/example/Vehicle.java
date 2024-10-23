package org.example;

public interface Vehicle {
    public void fillTank();
    public void drive();
    public String getFuelType();
    public int getNumOfWheels();
    public String getName();

    public void setId(Long id);
    public Long getId();
}
