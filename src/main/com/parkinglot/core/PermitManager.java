package com.parkinglot.core;

import com.parkinglot.enums.CarType;
import com.parkinglot.enums.PermitDurations;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

public class PermitManager {
    private final List<Customer> customers = new ArrayList<>();
    private final List<Car> cars = new ArrayList<>();

    private final HashMap<String, List<Permit>> permits = new HashMap<>();

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
                Permit existingPermit = car.getPermitForLot(lotId);
                permits.computeIfAbsent(license, k -> new ArrayList<>())
                        .add(existingPermit);
                return car;
            }

            String permitId = generatePermitId(customer.getCustomerId());
            car.updatePermit(lotId, permitId, expiration);
            Permit existingPermit = car.getPermitForLot(lotId);
            permits.computeIfAbsent(license, k -> new ArrayList<>())
                    .add(existingPermit);
            return car;
        }

        Car car = new Car(license, carType, customer);
        String permitId = generatePermitId(customer.getCustomerId());
        car.updatePermit(lotId, permitId, expiration);
        cars.add(car);
        Permit existingPermit = car.getPermitForLot(lotId);
        permits.computeIfAbsent(license, k -> new ArrayList<>())
                .add(existingPermit);
        return car;
    }

    public List<Car> getCars() {
        return cars;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Permit> getPermitsByCar(String license) {
        if (permits.containsKey(license)) {
            return permits.get(license);
        }
        throw new IllegalArgumentException("No Permit lot found with associated license plate: " + license);
    }

    // Generate a random Customer ID with name Concat
    private String generateCustomerId(String name) {
        return UUID.randomUUID().toString() + name.replaceAll("\\s+", "").toLowerCase();
    }

    private String generatePermitId(String customerId) {
        return UUID.randomUUID().toString() + customerId.replace("-", "");
    }

}
