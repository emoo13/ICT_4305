package com.parkinglot.utils;

import com.parkinglot.core.Address;
import com.parkinglot.core.Car;
import com.parkinglot.core.Customer;
import com.parkinglot.enums.CarType;
import com.parkinglot.service.ParkingOffice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParkingChargeIT {
    private Car car;

    @BeforeEach
    public void setUp()
    {
        Address officeAddress = new Address("125 West Drive",
                "", "Denver", "Colorado", "80210");
        ParkingOffice officeData = new ParkingOffice("Campus Parking Office", officeAddress);
        Address customerAddress = new Address("123 West Drive",
                "", "Denver", "Colorado", "80210");
        Customer customer = officeData.register("John Doe", customerAddress, "123-456-7890");
         car = new Car("DEN-UNI1", CarType.COMPACT, customer);
    }

    @Test
    void testEntryCharge()
    {
        ParkingCharge parkingCharge = ParkingCharge.entryCharge(
                "FAKEPERMIT", "PLN-1", Instant.now(), car);

        assertEquals("FAKEPERMIT", parkingCharge.getPermitId());
        assertEquals("PLN-1", parkingCharge.getLotId());
        assertEquals(12.40, parkingCharge.getAmount().getDollars());
    }

    @Test
    void testExitCharge()
    {
        ParkingCharge parkingCharge = ParkingCharge.entryCharge(
                "FAKEPERMIT2", "PLS-1", Instant.now(), car);
        ParkingCharge parkingChargeExit = ParkingCharge.exitCharge(
                "FAKEPERMIT2", "PLS-1", Instant.now().plus(2, ChronoUnit.HOURS), car);

        assertEquals("FAKEPERMIT2", parkingChargeExit.getPermitId());
        assertEquals("PLS-1", parkingChargeExit.getLotId());
        assertEquals(5.20, parkingChargeExit.getAmount().getDollars());
    }
}
