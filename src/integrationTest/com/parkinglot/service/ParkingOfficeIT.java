package com.parkinglot.service;

import com.parkinglot.core.*;
import com.parkinglot.enums.CarType;
import com.parkinglot.enums.PermitDurations;
import com.parkinglot.utils.ParkingCharge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParkingOfficeIT {
    private ParkingOffice officeData;
    private Address officeAddress;
    private Car customerCarData;
    private Address customerAddress;
    private Customer customer;

    @BeforeEach
    void setUp() throws Exception{
        officeAddress = new Address("125 West Drive",
                "", "Denver", "Colorado", "80210");
        officeData = new ParkingOffice("Campus Parking Office", officeAddress);
        officeData.setParkingLots();

        customerAddress = new Address("456 West Drive",
                "", "Denver", "Colorado", "80210");
        customer = officeData.register("Jane Smith", customerAddress, "098-765-4321");

        Car customerCar = new Car("DEN-UNI2", CarType.SUV, customer);
        customerCarData = officeData.register(customer, "PLN-2", customerCar.getLicense(),
                customerCar.getCarType(), PermitDurations.YEARLY);

    }

    @Test
    void testParkingOfficeDataSet() {
        List<ParkingLot> parkingLots = officeData.getParkingLots();

        assertEquals(2, parkingLots.size());
        assertEquals(officeAddress, officeData.getAddress());
        assertEquals("Campus Parking Office", officeData.getName());
    }

    @Test
    void testRegisterCar() {
        assertTrue(customerCarData.getPermit().contains("janesmith"));
        assertEquals("DEN-UNI2", customerCarData.getLicense());
        assertEquals("PLN-2", customerCarData.getPermitLot());
        assertEquals("Jane Smith", customerCarData.getOwner().getCustomerName());
        assertEquals(CarType.SUV, customerCarData.getCarType());
        assertEquals(5, customerCarData.getPermitExpiration().atZone(ZoneOffset.UTC).toLocalDate().getMonthValue());
        assertEquals(31, customerCarData.getPermitExpiration().atZone(ZoneOffset.UTC).toLocalDate().getDayOfMonth());
    }

    @Test
    void testRegisterCustomer() {
        assertTrue(customer.getCustomerId().contains("janesmith"));
        assertEquals("Jane Smith", customer.getCustomerName());
        assertEquals(customerAddress, customer.getCustomerAddress());
        assertEquals("098-765-4321", customer.getCustomerPhoneNumber());
    }

    @Test
    void testGetCustomers() {
        List<Customer> customers = officeData.getCustomers();
        assertEquals(1, customers.size());
    }

    @Test
    void testGetCars() {
        List<Car> cars = officeData.getCars();
        assertEquals(1, cars.size());
    }

    @Test
    void testAddCharge() {
        ParkingCharge ch = new ParkingCharge("P1Test","PLN-1", Instant.EPOCH, new Money(1200));
        Money amount = officeData.addCharge(ch);
        assertEquals(12.00, amount.getDollars());
    }
}
