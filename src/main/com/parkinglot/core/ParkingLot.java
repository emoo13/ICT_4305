package com.parkinglot.core;

import com.parkinglot.utils.ParkingCharge;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ParkingLot {
    private final String lotId;
    private final Address address;
    private final int capacity;
    private final Map<String, Instant> activeParking = new HashMap<>();
    private ParkingCharge parkingCharge;

    /**
     * Parking lot the university owns and the data associated to it
     * @param lotId - ID of the lot the car is parking in
     * @param address - Address of the lot
     * @param capacity - Capacity of cars allowed on lot
     */
    public ParkingLot(String lotId, Address address, int capacity) {
        this.lotId = lotId;
        this.address = address;
        this.capacity = capacity;
    }

    public String getLotId() {
        return lotId;
    }

    /**
     * Scan and retrieve car data on entry with manual dateTime
     * @param car - Car return of car data
     */
    public ParkingCharge entry(Instant entryTime, Car car) {
        permitValidity(car);
        activeParking.put(car.getLicense(), entryTime);
        System.out.println("--Car entered parking lot-- \n" +
                car.toString());

        return ParkingCharge.entryCharge(
                car.getPermit(), lotId, entryTime, car);

    }

    /**
     * If a parking lot is hourly, we need to calculate hours parked upon exit and entry
     * @param car - return of exiting car data
     * @return
     */
    public ParkingCharge exit(Instant checkoutTime, Car car) {
        Instant timeNow = Instant.now();
        Instant start = activeParking.remove(car.getLicense());
        if (start == null) {
            throw new IllegalStateException("Car with license plate " + car.getLicense() + " has no active parking status.");
        }

        if (start.isAfter(timeNow)) {
            throw new IllegalStateException("Exit time is before entry time--contact parking office with issues.");
        }
        System.out.println("--Car is leaving parking lot-- \n" +
                car);

        return ParkingCharge.exitCharge(
                car.getPermit(), lotId, checkoutTime, car);
    }

    private void permitValidity(Car car) {
        if (car.getPermit() == null || car.getPermit().isBlank()) {
            throw new IllegalArgumentException("Invalid car permit!");
        }
        if (car.getPermitExpiration() == null || car.getPermitExpiration().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Car Permit has expired!\nExpired: " + car.getPermitExpiration() + "\n Please re-register your car!");
        }
    }

    @Override
    public String toString() {
        return "--Parking Lot Data-- \n" +
                "------------------\n" +
                "Lot ID: " + lotId + "\n" +
                "Lot Address: " + address + "\n" +
                "Lot Capacity: " + capacity;
    }
}
