package com.parkinglot.core;

import com.parkinglot.enums.CarType;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Car {
    private final Map<String, Permit> permits = new HashMap<>();
    private final String license;
    private final Customer owner;
    private final CarType carType;
    private String currentLotId;

    /**
     * Register a new car
     * @param license - License plate of the car
     * @param type - Type of the car, whether compact
     * @param owner - owner name/data
     */
    public Car(String license, CarType type, Customer owner) {
        this.license = license;
        this.carType = type;
        this.owner = owner;
    }

    public boolean hasPermitForLot(String lotId) {
        return permits.containsKey(lotId);
    }

    // Setters

    public void setCurrentLot(String currentLotId) {
        this.currentLotId = currentLotId;
    }

    public void updatePermit(String lotId, String permitId, Instant expiration) {
        permits.put(lotId, new Permit(permitId, lotId, license, expiration));
        currentLotId  = lotId;
    }

    public void updatePermitExpiration(String lotId, Instant expiration) {
        Objects.requireNonNull(lotId);
        Objects.requireNonNull(expiration);
        Permit oldPermit = permits.get(lotId);
        if (oldPermit == null) {
            throw new IllegalStateException("No permit exists for lot " + lotId);
        }
        permits.put(lotId, new Permit(oldPermit.permitId(), lotId, license, expiration));
        currentLotId  = lotId;
    }

    public Permit getPermitForLot(String lotId) {
        return permits.get(lotId);
    }

    public Collection<String> getAllPermitIds() {
        return permits.values().stream().map(Permit::permitId).toList();
    }


    // Getters
    public String getLicense() {
        return license;
    }

    public String getPermit() {
        Permit p = currentLotId == null ? null : permits.get(currentLotId);
        return p == null ? null : p.permitId();
    }

    public String getPermitLot() {
        return currentLotId;
    }

    public Instant getPermitExpiration() {
        Permit p = (currentLotId == null) ? null : permits.get(currentLotId);
        return (p == null) ? null : p.expiration();
    }

    public Customer getOwner() {
        return owner;
    }

    public CarType getCarType() {
        return carType;
    }

    @Override
    public boolean equals(Object license) {
        if (this == license) {
            return true;
        }

        if (!(license instanceof Car that)) {
            return false;
        }

        return Objects.equals(this.getLicense(), that.license);
    }

    @Override
    public int hashCode() {
        return Objects.hash(license);
    }

    /** Return car data as String
     * @return Car description as String
     */
    @Override
    public String toString() {
        return "--Car Data-- \n" +
                "------------------\n" +
                "Permits: " + permits + "\n" +
                "License Plate: " + license + "\n" +
                "Owner: " + owner + "\n" +
                "Car Type: " + carType.toString();
    }
}
