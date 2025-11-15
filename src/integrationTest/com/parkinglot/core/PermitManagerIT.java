package com.parkinglot.core;

import com.parkinglot.enums.CarType;
import com.parkinglot.enums.PermitDurations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PermitManagerIT {
    private PermitManager permitManager;
    private Address customerAddress;
    private Customer customer;
    private Car car;

    @BeforeEach
    public void setup() {
        permitManager = new PermitManager();
        customerAddress = new Address("456 West Drive",
                "", "Denver", "Colorado", "80210");
        customer = permitManager.register("Jane Smith", customerAddress, "098-765-4321");
        car = permitManager.register(customer, "PLN-1", "DEN-UNI2",
                CarType.SUV, PermitDurations.YEARLY);

    }

    @Test
    void registerAllTest() {
        assertNotNull(customer);
        assertEquals("Jane Smith", customer.getCustomerName());
        assertEquals(customerAddress, customer.getCustomerAddress());
        assertEquals("098-765-4321", customer.getCustomerPhoneNumber());
        assertEquals("DEN-UNI2", car.getLicense());
        assertEquals(CarType.SUV, car.getCarType());
        assertEquals(customer, car.getOwner());
    }

    @Test
    void getCarTest() {
        List<Car> cars = permitManager.getCars();
        assertEquals(1, cars.size());
    }

    @Test
    void getCustomerTest() {
        List<Customer> customers = permitManager.getCustomers();
        assertEquals(1, customers.size());
    }

    @Test
    void getCarByPermitTest() {
        List<Permit> permits = permitManager.getPermitsByCar("DEN-UNI2");
        assertEquals(1, permits.size());
    }
}
