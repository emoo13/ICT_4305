package com.parkinglot;

import java.time.LocalDate;
import java.util.UUID;

public class ParkingHandler {

    public Car createPermit(String owner, String lotId, String license, CarType carType, PermitDurations expiryDate) {
        // Setup permit data
        Car carRegistryData = new Car(license, carType, owner);
        String permitId = generatePermitId(owner);

        // Create the permit based on inputs
        carRegistryData.setPermit(permitId);
        carRegistryData.setPermitLot(lotId);
        carRegistryData.setPermitExpiration(expiryDate.nextOccurrenceStartingNow(LocalDate.now()).end());
        return carRegistryData;
    }

    private String generatePermitId(String customerId) {
        return UUID.randomUUID().toString() + customerId.replace("-", "");
    }
}
