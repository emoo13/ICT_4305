package com.parkinglot.service;

import com.parkinglot.core.*;
import com.parkinglot.enums.CarType;
import com.parkinglot.enums.ParkingLots;
import com.parkinglot.enums.PermitDurations;
import com.parkinglot.utils.ParkingCharge;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

public class ParkingOffice {
    private final String name;
    private final Address address;
    private final List<Customer> customers = new ArrayList<>();
    private final List<Car> cars = new ArrayList<>();
    private List<ParkingLot> parkingLots = new ArrayList<>();
    private final List<ParkingCharge> parkingCharges = new ArrayList<>();

    public ParkingOffice(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public Customer register(String name, Address address, String phone) {
        String customerId = generateCustomerId(name);
        Customer customer = new Customer(customerId, name, address, phone);
        customers.add(customer);
        return customer;
    }

    public Car register(Customer customer, String lotId, String license, CarType carType, PermitDurations expiryDate) {
        // Setup permit data
        Car carRegistryData = new Car(license, carType, customer);
        String permitId = generatePermitId(customer.getCustomerId());

        // Create the permit based on inputs
        carRegistryData.setPermit(permitId);
        carRegistryData.setPermitLot(lotId);
        carRegistryData.setPermitExpiration(expiryDate.nextOccurrenceStartingNow(LocalDate.now()).end().atStartOfDay(ZoneOffset.UTC).toInstant());
        cars.add(carRegistryData);
        return carRegistryData;
    }

    public void processParkingLotEntry(Car car, String lotId) {
        Instant entryTime = Instant.now();
        ParkingLot lotData = retrieveLotData(lotId);
        ParkingCharge entryCharge = lotData.entry(entryTime, car);

        if (entryCharge.getAmount().getCents() != 0) {
            addCharge(entryCharge); // record non-zero entry fees
        }
    }

    public void processParkingLotExit(Car car, String lotId) {
        Instant exitTime = Instant.now();
        ParkingLot lotData = retrieveLotData(lotId);
        ParkingCharge exitCharge = lotData.exit(exitTime, car);

        if (exitCharge.getAmount().getCents() != 0) {
            addCharge(exitCharge);
        }
    }

    // Getters
    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Car> getCars() {
        return cars;
    }

    public List<ParkingLot> getParkingLots() {
        return parkingLots;
    }

    public List<ParkingCharge> getParkingCharges() {
        return parkingCharges;
    }

    public void setParkingLots() {
        parkingLots = new ArrayList<>(Arrays.stream(ParkingLots.values())
                .map(e -> new ParkingLot(
                        e.getLotId(),
                        e.getAddress(),
                        e.getLotCapacity()))
                .toList());
    }

    public ParkingLot retrieveLotData(String lotId) {
        return parkingLots.stream()
                .filter(l -> l.getLotId().equals(lotId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown lotId: " + lotId));
    }

    public Money addCharge(ParkingCharge parkingCharge) {
        parkingCharges.add(parkingCharge);
        return parkingCharge.getAmount();
    }

    // Generate a random Customer ID with name Concat
    private String generateCustomerId(String name) {
        return UUID.randomUUID().toString() + name.replaceAll("\\s+", "").toLowerCase();
    }

    private String generatePermitId(String customerId) {
        return UUID.randomUUID().toString() + customerId.replace("-", "");
    }
}
