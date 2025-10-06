package com.parkinglot;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ParkingLot {
    private final String lotId;
    private final Address address;
    private final int capacity;
    private final double lotPrice;
    private final boolean hourly;
    private final Map<String, LocalDateTime> activeParking = new HashMap<>();
    private double totalPrice;
    private final double overnightFee;

    /**
     * Parking lot the university owns and the data associated to it
     * @param lotId - ID of the lot the car is parking in
     * @param address - Address of the lot
     * @param capacity - Capacity of cars allowed on lot
     * @param lotPrice  - Price per hour parking in
     */
    public ParkingLot(String lotId, Address address, int capacity, double lotPrice, boolean hourly, double overnightFee) {
        this.lotId = lotId;
        this.address = address;
        this.capacity = capacity;
        this.lotPrice = lotPrice;
        this.hourly = hourly;
        this.overnightFee = overnightFee;
    }

    public ParkingLot(String lotId, Address address, int capacity, double lotPrice, boolean hourly) {
        this(lotId, address, capacity, lotPrice, hourly, /*overnight*/ 0.0);
    }

    /**
     * Scan and retrieve car data on entry
     * @param car - Car return of car data
     */
    public void entry(Car car) {
        entry(car, LocalDateTime.now());
    }

    /**
     * Scan and retrieve car data on entry with manual dateTime
     * @param car - Car return of car data
     */
    public void entry(Car car, LocalDateTime dateTime) {
        permitValidity(car);
        activeParking.put(car.getLicense(), LocalDateTime.now());
        if (hourly) {
            totalPrice = 0.0;
        } else {
            totalPrice = calculateDiscount(car, lotPrice);
        }

        System.out.println("--Car entered parking lot-- \n" +
                car.toString());
    }

    /**
     * If a parking lot is hourly, we need to calculate hours parked upon exit and entry
     * @param car - return of exiting car data
     * @return
     */
    public void exit(Car car) {
        exit(car, LocalDateTime.now());
    }

    public void exit(Car car, LocalDateTime dateTime) {
        LocalDateTime start = activeParking.remove(car.getLicense());
        if (start == null) {
            throw new IllegalStateException("Car with license plate " + car.getLicense() + " has no active parking status.");
        }
        if (start.isAfter(dateTime)) {
            throw new IllegalStateException("Exit time is before entry time--contact parking office with issues.");
        }

        if (hourly) {
            // Calculate how many hours parked there
            long minutesParked = Duration.between(start, dateTime).toMinutes();
            long hoursParked = Math.max(1, (minutesParked + 59) / 60);

            // In a real world scenario, it would charge the user
            double totalAfterHours = hoursParked * lotPrice;
            totalPrice = calculateDiscount(car, totalAfterHours);
        } else {
            int overnightParked = Math.toIntExact(Duration.between(start.toLocalDate().atStartOfDay(),
                    dateTime.toLocalDate().atStartOfDay()).toDays());
            double overnightCharge = calculateDiscount(car, overnightParked * overnightFee);
            totalPrice = totalPrice + overnightCharge;
        }
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Determine if car qualifies for 20% discount
     * @param car - Car data based on lot entry/exit
     * @param price - total price after discount
     * @return total price after any applicable discounts
     */
    private double calculateDiscount(Car car, double price) {
        // Discount is 20%
        if (car.getCarType() == CarType.COMPACT) {
            return price * 0.8;
        }
        return price;
    }

    private void permitValidity(Car car) {
        if (car.getPermit() == null || car.getPermit().isBlank()) {
            throw new IllegalArgumentException("Invalid car permit!");
        }
        if (car.getPermitExpiration() == null || car.getPermitExpiration().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Car Permit has expired!\nExpired: " + car.getPermitExpiration() + "\n Please re-register your car!");
        }
    }

    @Override
    public String toString() {
        return "--Parking Lot Data-- \n" +
                "------------------\n" +
                "Lot ID: " + lotId + "\n" +
                "Lot Address: " + address + "\n" +
                "Lot Capacity: " + capacity + "\n" +
                "Lot Price: " + lotPrice;
    }
}
