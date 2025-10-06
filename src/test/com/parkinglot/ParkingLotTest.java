package com.parkinglot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingLotTest {
    private ParkingLot parkingLotNorth;
    private ParkingLot parkingLotSouth;
    private Car customer1Car1Data;
    private Car customer1Car2Data;
    private Car customer2Car2Data;
    private Car customer2Car1;

    @BeforeEach
    void setUp() throws Exception{
        ParkingHandler handler = new ParkingHandler();

        // -- Set up parking lot rates --
        // North lot is not hourly but instantiates an overnight fee
        parkingLotNorth = new ParkingLot("PLN-1",
                new Address("123 Fake Street",
                        "", "Denver", "Colorado", "80210"),
                150, 15.50, false, 10.0);
        // South lot is hourly but has no overnight fee
        parkingLotSouth = new ParkingLot("PLN-2",
                new Address("456 Null Street",
                        "", "Denver", "Colorado", "80210"),
                250, 3.25, true);

        // Setup Customer 1's first car
        Customer customer1 = new Customer("Cus-1", "John Doe",
                new Address("123 West Drive",
                        "", "Denver", "Colorado", "80210"),
                "123-456-7890");
        Car customer1Car1 = new Car("DEN-UNI1", CarType.COMPACT, customer1.getCustomerId());
        customer1Car1Data = handler.createPermit(customer1.getCustomerId(), "PLN-1", customer1Car1.getLicense(),
                customer1Car1.getCarType(), PermitDurations.YEARLY);
        // Setup the customer 1's second car
        Car customer1Car2 = new Car("DEN-UNI6", CarType.SUV, customer1.getCustomerId());
        customer1Car2Data = handler.createPermit(customer1.getCustomerId(), "PLN-1", customer1Car2.getLicense(),
                customer1Car2.getCarType(), PermitDurations.Q2);

        // Setup Customer 2's first and only car
        Customer customer2 = new Customer("Cus-2", "Jane Smith",
                new Address("456 West Drive",
                        "", "Denver", "Colorado", "80210"),
                "098-765-4321");
        customer2Car1 = new Car("DEN-UNI2", CarType.SUV, customer2.getCustomerId());
        customer2Car2Data = handler.createPermit(customer2.getCustomerId(), "PLN-2", customer2Car1.getLicense(),
                customer2Car1.getCarType(), PermitDurations.YEARLY);
    }

    @Test
    void testRegisterNewCarYearly() {
        // Test permit data
        assertTrue(customer1Car1Data.getPermit().contains("Cus1"));
        assertEquals("PLN-1", customer1Car1Data.getPermitLot());
        assertEquals(5, customer1Car1Data.getPermitExpiration().getMonthValue());
        assertEquals(31, customer1Car1Data.getPermitExpiration().getDayOfMonth());

        // Test Car data
        assertEquals("DEN-UNI1", customer1Car1Data.getLicense());
        assertEquals("Cus-1", customer1Car1Data.getOwner());
    }

    @Test
    void testExpiredPermit() {
        customer2Car1.setPermitExpiration(LocalDate.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> parkingLotNorth.entry(customer2Car1));
    }

    @Test
    void testLeavingLotNorthCompact() {
        LocalDateTime thisInstance = LocalDateTime.now();
        parkingLotNorth.entry(customer1Car1Data);
        parkingLotNorth.exit(customer1Car1Data, thisInstance);
        assertEquals(12.4, parkingLotNorth.getTotalPrice());
    }

    @Test
    void testLeavingLotNorthCompactTwoOvernight() {
        LocalDateTime thisInstance = LocalDateTime.now();
        LocalDateTime twoDaysLater = thisInstance.plusDays(2);
        parkingLotNorth.entry(customer1Car1Data, thisInstance);
        parkingLotNorth.exit(customer1Car1Data, twoDaysLater);

        assertEquals(28.4, parkingLotNorth.getTotalPrice());
    }

    @Test
    void testLeavingLotNorthSuv() {
        LocalDateTime thisInstance = LocalDateTime.now();
        parkingLotNorth.entry(customer1Car2Data);
        parkingLotNorth.exit(customer1Car2Data, thisInstance);
        assertEquals(15.5, parkingLotNorth.getTotalPrice());
    }

    @Test
    void testLeavingLotSouthSuv() {
        LocalDateTime thisInstance = LocalDateTime.now();
        parkingLotSouth.entry(customer2Car2Data, thisInstance);

        LocalDateTime dateTimeLater = thisInstance.plusHours(4);
        parkingLotSouth.exit(customer2Car2Data, dateTimeLater);
        assertEquals(13.0, parkingLotSouth.getTotalPrice());
    }

    @Test
    void testLeavingLotSouthCompact() {
        LocalDateTime thisInstance = LocalDateTime.now();
        parkingLotSouth.entry(customer1Car1Data, thisInstance);

        LocalDateTime dateTimeLater = thisInstance.plusHours(4);
        parkingLotSouth.exit(customer1Car1Data, dateTimeLater);
        assertEquals(10.4, parkingLotSouth.getTotalPrice());
    }

}
