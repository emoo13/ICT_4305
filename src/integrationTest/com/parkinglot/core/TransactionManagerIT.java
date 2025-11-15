package com.parkinglot.core;

import com.parkinglot.enums.CarType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TransactionManagerIT {
    private TransactionManager transactionManager;
    private ParkingTransaction transaction;
    private Permit permit;
    private ParkingLot lot;

    @BeforeEach
    public void setup() {
        Calendar now = Calendar.getInstance();

        // permit that matches your record/class
        Instant expiration = now.toInstant()
                .atZone(ZoneId.systemDefault())  // or a specific ZoneId
                .plusMonths(2)
                .toInstant();
        transactionManager = new TransactionManager();
        permit = new Permit("FakePermit", "PLN-1", "DEN-UNI2", expiration);
        lot = new ParkingLot("PLN-1",
                new Address("Fake Address Street", "", "Denver", "CO", "80122"),
                150);
        Car car = new Car("DEN-UNI2", CarType.SUV, new Customer("C1", "Jane Doe",
                new Address("123 Street", "", "Denver", "CO", "00000"), "123-456-7890"));
        car.updatePermit("PLN-1", "FakePermit", expiration);
        transaction = transactionManager.park(now, permit, lot, car);
    }

    @Test
    void testPark() {
        Money expectedFee = new Money(1550);
        assertNotNull(transaction);
        assertEquals(permit, transaction.getPermit());
        assertEquals(lot, transaction.getLot());
        assertEquals(expectedFee.getCents(), transaction.getFeeCharged().getCents());
    }

    @Test
    void getParkingChargesTest() {
        Money expectedFee = new Money(1550);
        Money actualAmount = transactionManager.getParkingCharges(permit);
        assertNotNull(permit);
        assertEquals(expectedFee.getCents(), actualAmount.getCents());
    }

    @Test
    void getParkingChargesByLicenseTest() {
        Money expectedFee = new Money(1550);
        Money actualAmount = transactionManager.getParkingCharges("DEN-UNI2");
        assertNotNull(permit);
        assertEquals(expectedFee.getCents(), actualAmount.getCents());
    }

}
