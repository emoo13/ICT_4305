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
        // Only register if customer doesn't exist in DB yet. Comparing by phone, since names can be dupe
        Optional<Customer> customerMatch = customers.stream()
                .filter(c -> Objects.equals(c.getCustomerPhoneNumber(), phone))
                .findFirst();

        if (customerMatch.isPresent()) {
            return customerMatch.get();
        }

        String customerId = generateCustomerId(name);
        Customer customer = new Customer(customerId, name, address, phone);
        customers.add(customer);
        return customer;
    }

    public Car register(Customer customer, String lotId, String license, CarType carType, PermitDurations expiryDate) {
        Instant expiration = expiryDate
                .nextOccurrenceStartingNow(LocalDate.now())
                .end()
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        Optional<Car> carMatch = cars.stream()
                .filter(c -> Objects.equals(c.getLicense(), license))
                .findFirst();

        if (carMatch.isPresent()) {
            Car car = carMatch.get();

            if (car.hasPermitForLot(lotId)) {
                car.setCurrentLot(lotId);
                return car;
            }

            String permitId = generatePermitId(customer.getCustomerId());
            car.updatePermit(lotId, permitId, expiration);
            return car;
        }

        Car car = new Car(license, carType, customer);
        String permitId = generatePermitId(customer.getCustomerId());
        car.updatePermit(lotId, permitId, expiration);
        cars.add(car);
        return car;
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

    public Collection<String> getCustomerIds() {
        return customers.stream()
                .map(Customer::getCustomerId)
                .toList();
    }

    public Collection<String> getPermitIds() {
        return cars.stream()
                .flatMap(c -> c.getAllPermitIds().stream())
                .toList();
    }
    public Collection<String> getPermitIds(Customer customer) {
        String cid = customer.getCustomerId();
        return cars.stream()
                .filter(c -> c.getOwner() != null
                        && cid.equals(c.getOwner().getCustomerId()))
                .flatMap(c -> c.getAllPermitIds().stream())
                .toList();
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
