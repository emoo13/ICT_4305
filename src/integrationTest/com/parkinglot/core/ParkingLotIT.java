package com.parkinglot.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkinglot.enums.CarType;
import com.parkinglot.enums.PermitDurations;
import com.parkinglot.service.ParkingOffice;
import com.parkinglot.utils.ParkingCharge;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParkingLotIT {
    private ParkingLot parkingLotNorth;
    private ParkingLot parkingLotSouth;
    private Car customer1Car1Data;
    private Car customer1Car2Data;
    private Car customer2Car1Data;
    private Car customer2Car1;

    @BeforeEach
    void setUp() throws Exception{
        Address officeAddress = new Address("125 West Drive",
                "", "Denver", "Colorado", "80210");
        ParkingOffice officeData = new ParkingOffice("Campus Parking Office", officeAddress);
        officeData.setParkingLots();
        List<ParkingLot> parkingLots = officeData.getParkingLots();

        parkingLotNorth = parkingLots.stream()
                .filter(l -> "PLN-1".equals(l.getLotId()))
                .findFirst().orElseThrow();
        parkingLotSouth = parkingLots.stream()
                .filter(l -> "PLS-1".equals(l.getLotId()))
                .findFirst().orElseThrow();

        //---- CUSTOMER 1 ----
        // Setup Customer 1's first car
        Address customerAddress1 = new Address("123 West Drive",
                "", "Denver", "Colorado", "80210");
        Customer customer1 = officeData.register("John Doe", customerAddress1, "123-456-7890");
        Car customer1Car1 = new Car("DEN-UNI1", CarType.COMPACT, customer1);
        customer1Car1Data = officeData.register(customer1, "PLN-1", customer1Car1.getLicense(),
                customer1Car1.getCarType(), PermitDurations.YEARLY);

        // Setup the customer 1's second car
        Car customer1Car2 = new Car("DEN-UNI6", CarType.SUV, customer1);
        customer1Car2Data = officeData.register(customer1, "PLN-1", customer1Car2.getLicense(),
                customer1Car2.getCarType(), PermitDurations.Q2);

        //---- CUSTOMER 2 ----
        // Setup Customer 2's first and only car
        Address customerAddress2 = new Address("456 West Drive",
                "", "Denver", "Colorado", "80210");
        Customer customer2 = officeData.register("Jane Smith", customerAddress2, "098-765-4321");
        customer2Car1 = new Car("DEN-UNI2", CarType.SUV, customer2);
        customer2Car1Data = officeData.register(customer2, "PLN-2", customer2Car1.getLicense(),
                customer2Car1.getCarType(), PermitDurations.YEARLY);

    }

    @Test
    void testExpiredPermit() {
        customer2Car1.setPermitExpiration(Instant.now().minus(1, ChronoUnit.HOURS));
        assertThrows(IllegalArgumentException.class, () -> parkingLotNorth.entry(Instant.now(), customer2Car1));
    }

    /*
        Only charge on entry, no overnight
     */
    @Test
    void testLeavingLotNorthCompact() {
        Instant entryTime = Instant.now();
        Instant exitTime = entryTime.plus(2, ChronoUnit.HOURS);
        parkingLotNorth.entry(entryTime, customer1Car1Data);
        ParkingCharge exitCharge = parkingLotNorth.exit(exitTime, customer1Car1Data);
        assertEquals(0, exitCharge.getAmount().getCents());
    }

    @Test
    void testLeavingLotNorthCompactTwoOvernight() {
        Instant thisInstance = Instant.now();
        Instant twoDaysLater = thisInstance.plus(2, ChronoUnit.DAYS);

        ParkingCharge entryCharge = parkingLotNorth.entry(Instant.now(), customer1Car1Data);
        ParkingCharge exitCharge = parkingLotNorth.exit(twoDaysLater, customer1Car1Data);

        assertEquals(12.40, entryCharge.getAmount().getDollars());
        assertEquals(16.00, exitCharge.getAmount().getDollars());
    }

    @Test
    void testLeavingLotNorthSuv() {
        Instant thisInstance = Instant.now();
        ParkingCharge entryCharge = parkingLotNorth.entry(Instant.now(), customer1Car2Data);
        ParkingCharge exitCharge = parkingLotNorth.exit(thisInstance, customer1Car2Data);

        assertEquals(15.5, entryCharge.getAmount().getDollars());
        assertEquals(0.00, exitCharge.getAmount().getDollars());
    }

    @Test
    void testLeavingLotSouthSuv() {
        Instant thisInstance = Instant.now();
        Instant fourHoursLater = thisInstance.plus(4, ChronoUnit.HOURS);
        ParkingCharge entryCharge = parkingLotSouth.entry(Instant.now(), customer2Car1Data);
        ParkingCharge exitCharge = parkingLotSouth.exit(fourHoursLater, customer2Car1Data);

        assertEquals(0.00, entryCharge.getAmount().getDollars());
        assertEquals(13.00, exitCharge.getAmount().getDollars());
    }

    @Test
    void testLeavingLotSouthCompact() {
        Instant thisInstance = Instant.now();
        Instant fourHoursLater = thisInstance.plus(4, ChronoUnit.HOURS);
        ParkingCharge entryCharge = parkingLotSouth.entry(Instant.now(), customer1Car1Data);
        ParkingCharge exitCharge = parkingLotSouth.exit(fourHoursLater, customer1Car1Data);

        assertEquals(0.00, entryCharge.getAmount().getDollars());
        assertEquals(10.40, exitCharge.getAmount().getDollars());
    }

    }
