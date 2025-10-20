package com.parkinglot.core;

import com.parkinglot.enums.CarType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.parkinglot.utils.ParkingCharge;
import org.mockito.MockedStatic;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mockStatic;

public class ParkingLotTest {
    private ParkingLot parkingLot;
    private Car car;
    @Mock
    private ParkingCharge parkingCharge;

    @BeforeEach
    void setUp() throws Exception {
        parkingLot = new ParkingLot("PLN-1", new Address("123", "", "Denver", "CO", "80210"), 100);
        Customer owner = new Customer("C-1", "Alice",
                new Address("x", "", "Denver", "CO", "80210"), "555-0000");
        car = new Car("ABC123", CarType.SUV, owner);
        car.setPermit("PERMIT-1");
        car.setPermitExpiration(Instant.now().plus(30, ChronoUnit.DAYS));
    }

    @Test
    void testEntry() {
        Instant entry = Instant.now();
        ParkingCharge parkingCharge = new ParkingCharge("PERMIT-1", "PLN-1", entry, new Money(0));

        try (MockedStatic<ParkingCharge> mocked = mockStatic(ParkingCharge.class)) {
            mocked.when(() -> ParkingCharge.entryCharge("PERMIT-1", "PLN-1", entry, car))
                    .thenReturn(parkingCharge);

            ParkingCharge entryCharge = parkingLot.entry(entry, car);

            assertSame(parkingCharge, entryCharge);
            mocked.verify(() -> ParkingCharge.entryCharge("PERMIT-1", "PLN-1", entry, car));
        }
    }

    @Test
    void testExit() {
        Instant entry = Instant.now();
        Instant exit = entry.plus(2, ChronoUnit.HOURS);
        parkingLot.entry(entry, car);

        ParkingCharge parkingCharge = new ParkingCharge("PERMIT-1", "PLN-1", exit, new Money(0));

        try (MockedStatic<ParkingCharge> mocked = mockStatic(ParkingCharge.class)) {
            mocked.when(() -> ParkingCharge.exitCharge("PERMIT-1", "PLN-1", exit, car))
                    .thenReturn(parkingCharge);

            ParkingCharge exitCharge = parkingLot.exit(exit, car);

            assertSame(parkingCharge, exitCharge);
            mocked.verify(() -> ParkingCharge.exitCharge("PERMIT-1", "PLN-1", exit, car));
            mocked.verifyNoMoreInteractions();
        }
    }
}