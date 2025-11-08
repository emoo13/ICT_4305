package com.parkinglot.service;

import com.parkinglot.core.*;
import com.parkinglot.enums.CarType;
import com.parkinglot.enums.PermitDurations;
import com.parkinglot.utils.ParkingCharge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingOfficeIT {
    private ParkingOffice officeData;
    private Address officeAddress;
    private Car customerCarData;
    private Car customerCarData2;
    private Car alreadyExistsCustomerCarData;
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
        customerCarData = officeData.register(customer, "PLN-1", customerCar.getLicense(),
                customerCar.getCarType(), PermitDurations.YEARLY);

        customerCarData2 = officeData.register(customer, "PLS-1", customerCar.getLicense(),
                customerCar.getCarType(), PermitDurations.YEARLY);

        alreadyExistsCustomerCarData = officeData.register(customer, "PLS-1", customerCar.getLicense(),
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
        assertSame(customerCarData, customerCarData2);
        assertSame(customerCarData, alreadyExistsCustomerCarData);
        assertEquals(1, officeData.getCars().size());

        // the car has permits for both lots
        assertTrue(customerCarData.hasPermitForLot("PLN-1"));
        assertTrue(customerCarData.hasPermitForLot("PLS-1"));

        assertEquals("DEN-UNI2", customerCarData.getLicense());
        assertEquals("Jane Smith", customerCarData.getOwner().getCustomerName());
        assertEquals(CarType.SUV, customerCarData.getCarType());

        var pln1 = customerCarData.getPermitForLot("PLN-1");
        assertNotNull(pln1);
        assertTrue(pln1.permitId().contains("janesmith"));
        assertEquals("PLN-1", pln1.lotId());
        assertEquals(5,  pln1.expiration().atZone(ZoneOffset.UTC).toLocalDate().getMonthValue());
        assertEquals(31, pln1.expiration().atZone(ZoneOffset.UTC).toLocalDate().getDayOfMonth());

        var pls1 = customerCarData.getPermitForLot("PLS-1");
        assertNotNull(pls1);
        assertTrue(pls1.permitId().contains("janesmith"));
        assertEquals("PLS-1", pls1.lotId());
        assertEquals(5,  pls1.expiration().atZone(ZoneOffset.UTC).toLocalDate().getMonthValue());
        assertEquals(31, pls1.expiration().atZone(ZoneOffset.UTC).toLocalDate().getDayOfMonth());

        customerCarData.setCurrentLot("PLN-1");
        assertEquals("PLN-1", customerCarData.getPermitLot());
        assertEquals(pln1.permitId(), customerCarData.getPermit());
        assertEquals(pln1.expiration(), customerCarData.getPermitExpiration());

        customerCarData.setCurrentLot("PLS-1");
        assertEquals("PLS-1", customerCarData.getPermitLot());
        assertEquals(pls1.permitId(), customerCarData.getPermit());
        assertEquals(pls1.expiration(), customerCarData.getPermitExpiration());
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
        assertEquals(1, officeData.getParkingCharges().size());
    }

    @Test
    void testGetPermitIds() {
        Collection<String> permitIds = officeData.getPermitIds();
        assertEquals(2, permitIds.size());
    }

    @Test
    void testGetPermitIdsForCustomer() {
        Collection<String> permitIds = officeData.getPermitIds(customer);
        assertEquals(2, permitIds.size());
    }

    @Test
    void testGetCustomerIds() {
        Collection<String> customerIds = officeData.getCustomerIds();
        assertEquals(1, customerIds.size());
    }

    @Test
    void testLotData() {
        ParkingLot lot = officeData.retrieveLotData("PLN-1");
        assertEquals("PLN-1", lot.getLotId());
        assertEquals(150, lot.getLotCapacity());
    }

    @Test
    void testInvalidLotData() {
        assertThrows(
                IllegalArgumentException.class,
                () -> officeData.retrieveLotData("DOESNTEXIST11")
        );
    }
}
