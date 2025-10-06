package com.parkinglot;

import java.time.LocalDate;

public class Car {
    private String permit;
    private String permitLot;
    private LocalDate permitExpiration;
    private final String license;
    private final String owner;
    private final CarType carType;

    /**
     * Register a new car
     * @param license - License plate of the car
     * @param type - Type of the car, whether compact
     * @param owner - owner name/data
     */
    public Car(String license, CarType type, String owner) {
        this.license = license;
        this.carType = type;
        this.owner = owner;
    }

    // Setters
    public void setPermit(String permit) {
        this.permit = permit;
    }
    public void setPermitExpiration(LocalDate permitExpiration) {
        this.permitExpiration = permitExpiration;
    }
    public void setPermitLot(String permitLot) {
        this.permitLot = permitLot;
    }

    // Getters
    public String getLicense() {
        return license;
    }
    public String getPermit() {
        return permit;
    }
    public String getPermitLot() {
        return permitLot;
    }
    public LocalDate getPermitExpiration() {
        return permitExpiration;
    }
    public String getOwner() {
        return owner;
    }
    public CarType getCarType() {
        return carType;
    }

    /** Return car data as String
     * @return Car description as String
     */
    @Override
    public String toString() {
        return "--Car Data-- \n" +
                "------------------\n" +
                "Permit: " + permit + "\n" +
                "Expiration: " + permitExpiration + "\n" +
                "License Plate: " + license + "\n" +
                "Owner: " + owner + "\n" +
                "Car Type: " + carType.toString();
    }
}
