package com.parkinglot.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParkingTransactionIT {
    private ParkingTransaction parkingTransaction;
    private Calendar now;
    private Permit permit;
    private ParkingLot lot;
    private Money cents;

    @BeforeEach
    void setUp() throws Exception{
        now = Calendar.getInstance();
        Instant twoMonthsLater = now.toInstant()
                .atZone(ZoneId.systemDefault())  // or a specific ZoneId
                .plusMonths(2)
                .toInstant();
        cents = new Money(2000);
        permit = new Permit("FakePermit", "PLN-1", "LIC-FAKE", twoMonthsLater);
        lot = new ParkingLot("PLN-1",
                new Address("Fake Address Street", "", "Denver", "CO", "80122"),
                150);
        parkingTransaction = new ParkingTransaction(now, permit, lot, cents);
    }

    @Test
    public void getTransactionDateTest() {
        Calendar date = parkingTransaction.getTransactionDate();
        assertEquals(now, date);
    }

    @Test
    public void getPermit() {
        Permit actualPermit = parkingTransaction.getPermit();
        assertEquals(permit, actualPermit);
    }

    @Test
    public void getLot() {
        ParkingLot actualLot = parkingTransaction.getLot();
        assertEquals(lot, actualLot);
    }

    @Test
    public void getFee() {
        Money actualFee = parkingTransaction.getFeeCharged();
        assertEquals(cents, actualFee);
    }
}
