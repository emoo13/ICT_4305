package com.parkinglot.service;

import com.parkinglot.core.*;
import com.parkinglot.enums.CarType;
import com.parkinglot.enums.ParkingLots;
import com.parkinglot.enums.PermitDurations;
import com.parkinglot.core.ParkingCharge;

import java.util.*;

public class ParkingOffice {
    private final String parkingOfficeName;
    private final Address parkingOfficeAddress;
    private List<ParkingLot> listOfParkingLots  = new ArrayList<>();
    private final List<ParkingCharge> parkingCharges = new ArrayList<>();
    private final PermitManager permitManager;
    private final TransactionManager transactionManager;

    public ParkingOffice(String parkingOfficeName, Address parkingOfficeAddress) {
        this(parkingOfficeName, parkingOfficeAddress,
                new PermitManager(), new TransactionManager());
    }

    public ParkingOffice(String parkingOfficeName, Address parkingOfficeAddress,
                         PermitManager permitManager,
                         TransactionManager transactionManager) {
        this.parkingOfficeName = parkingOfficeName;
        this.parkingOfficeAddress = parkingOfficeAddress;
        this.permitManager = permitManager;
        this.transactionManager = transactionManager;
    }

    public Car findCar(String permitId, String parkingLotId) {
        return getCars().stream()
                .filter(car -> permitId.equals(car.getPermitForLot(parkingLotId).getPermitId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
    }

    /**
     * Handle tracking who parked and when
     * @param dateParked
     * @param parkingPermit
     * @param parkingLot
     * @return ParkingTransaction
     */
    public ParkingTransaction park(Calendar dateParked, Permit parkingPermit, ParkingLot parkingLot) {
        Car permitToCar = findCar(parkingPermit.getPermitId(), parkingLot.getLotId());
        return transactionManager.park(dateParked, parkingPermit, parkingLot, permitToCar);
    }

    /**
     * Register Customer
     * @param name
     * @param address
     * @param phone
     * @return Customer
     */
    public Customer register(String name, Address address, String phone) {
        return permitManager.register(name, address, phone);
    }

    /**
     * Register Car for a permit
     * @param customer
     * @param lotId
     * @param license
     * @param carType
     * @param expiryDate
     * @return Car
     */
    public Car register(Customer customer, String lotId, String license,
                        CarType carType, PermitDurations expiryDate) {
        return permitManager.register(customer, lotId, license, carType, expiryDate);
    }

    // Getters
    public String getParkingOfficeName() {
        return parkingOfficeName;
    }

    public Address getAddress() {
        return parkingOfficeAddress;
    }

    public List<Customer> getCustomers() {
        return permitManager.getCustomers() ;
    }

    public List<Car> getCars() {
        return permitManager.getCars();
    }

    public List<ParkingLot> getParkingLots() {
        return listOfParkingLots ;
    }

    public ParkingLot getParkingLotById(String lotId) {
        for (ParkingLot lot : listOfParkingLots) {
            if (lot.getLotId().equals(lotId)) {
                return lot;
            }
        }
        throw new IllegalArgumentException("No parking lot found with id: " + lotId);
    }

    public List<ParkingCharge> getParkingCharges() {
        return parkingCharges;
    }

    public Collection<String> getCustomerIds() {
        return getCustomers().stream()
                .map(Customer::getCustomerId)
                .toList();
    }

    public Collection<String> getPermitIds() {
        return getCars().stream()
                .flatMap(c -> c.getAllPermitIds().stream())
                .toList();
    }

    public Collection<String> getPermitIds(Customer customer) {
        String cid = customer.getCustomerId();
        return getCars().stream()
                .filter(c -> c.getOwner() != null
                        && cid.equals(c.getOwner().getCustomerId()))
                .flatMap(c -> c.getAllPermitIds().stream())
                .toList();
    }

    public void setParkingLots() {
        listOfParkingLots = new ArrayList<>(Arrays.stream(ParkingLots.values())
                .map(e -> new ParkingLot(
                        e.getLotId(),
                        e.getAddress(),
                        e.getLotCapacity()))
                .toList());
    }

    public ParkingLot retrieveLotData(String lotId) {
        return listOfParkingLots.stream()
                .filter(l -> l.getLotId().equals(lotId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown lotId: " + lotId));
    }

    public Money addCharge(ParkingCharge parkingCharge) {
        parkingCharges.add(parkingCharge);
        return parkingCharge.getAmount();
    }
}
