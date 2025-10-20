package com.parkinglot.service;

import com.parkinglot.core.*;
import com.parkinglot.enums.CarType;
import com.parkinglot.utils.ParkingCharge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ParkingOfficeTest {
    private Address officeAddress;
    private Car customerCar;

    @Mock
    private ParkingLot lot;

    @Spy
    @InjectMocks
    private ParkingOffice officeData = new ParkingOffice("Campus Parking Office",
            new Address("125 West Drive","","Denver","Colorado","80210"));

    @BeforeEach
    void setUp() throws Exception{
        officeData.setParkingLots();

        Address customerAddress = new Address("456 West Drive",
                "", "Denver", "Colorado", "80210");
        Customer customer = officeData.register("Jane Smith", customerAddress, "098-765-4321");

        customerCar = new Car("DEN-UNI2", CarType.SUV, customer);

    }

    @Test
    void testProcessEntryAndGetCharges() {
        ParkingOffice office = spy(new ParkingOffice("Office", officeAddress));
        ParkingLot lot = mock(ParkingLot.class);
        doReturn(lot).when(office).retrieveLotData("PLN-1");

        // Expecting no charge, since it's 0
        ParkingCharge ch = new ParkingCharge("P1Test","PLN-1", Instant.EPOCH, new Money(0));
        when(lot.entry(any(Instant.class), eq(customerCar))).thenReturn(ch);

        office.processParkingLotEntry(customerCar, "PLN-1");

        verify(lot).entry(any(Instant.class), eq(customerCar));
        verify(office, never()).addCharge(any());

        List<ParkingCharge> charges = office.getParkingCharges();
        assertEquals(0, charges.size());
    }

    @Test
    void testProcessExitAndGetCharges() {
        ParkingOffice office = spy(new ParkingOffice("Office", officeAddress));
        ParkingLot lot = mock(ParkingLot.class);
        doReturn(lot).when(office).retrieveLotData("PLN-1");

        // Expecting 1 charge
        ParkingCharge ch = new ParkingCharge("P1Test","PLN-1", Instant.EPOCH, new Money(1200));
        when(lot.exit(any(Instant.class), eq(customerCar))).thenReturn(ch);

        office.processParkingLotExit(customerCar, "PLN-1");

        verify(lot).exit(any(Instant.class), eq(customerCar));

        List<ParkingCharge> charges = office.getParkingCharges();
        assertEquals(1, charges.size());
        assertEquals("PLN-1", charges.getFirst().getLotId());
        assertEquals("P1Test", charges.getFirst().getPermitId());
    }

}
