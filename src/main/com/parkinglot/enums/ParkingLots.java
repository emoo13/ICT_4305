package com.parkinglot.enums;

import com.parkinglot.core.Address;
import com.parkinglot.core.Money;

public enum ParkingLots {
    // Not hourly, charges overnight fee
    PLN1("PLN-1", ChargingMode.ENTRY_ONLY,
            "123 Fake Street",
            "",
            "80210",
            150,
            1550,
            1000,
            0),
    // Hourly Lot, no overnight fee
    PLS1("PLS-1", ChargingMode.ENTRY_EXIT,
            "123 West Drive",
            "Lot B",
            "80210",
            250,
            0,
            0,
            325);

    private final String lotId;
    private final ChargingMode chargingMode;
    private final String streetAddress;
    private final String streetAddress2;
    private final String zipCode;
    private final int lotCapacity;
    private final long entryFeeCents;
    private final long overnightCents;
    private final long hourlyCents;

    ParkingLots(String lotId, ChargingMode chargingMode, String streetAddress, String streetAddress2, String zipCode,
                int lotCapacity, long entryFeeCents, long overnightCents, long hourlyCents) {
        this.lotId = lotId;
        this.chargingMode = chargingMode;
        this.streetAddress = streetAddress;
        this.streetAddress2 = streetAddress2;
        this.zipCode = zipCode;
        this.lotCapacity = lotCapacity;
        this.entryFeeCents = entryFeeCents;
        this.overnightCents = overnightCents;
        this.hourlyCents = hourlyCents;
    }

    public ChargingMode getChargingMode() {
        return chargingMode;
    }

    public Money getOvernightFee() {
        return new Money(overnightCents);
    }

    public Money getEntryFee() {
        return new Money(entryFeeCents);
    }

    public Money getHourlyFee() {
        return new Money(hourlyCents);
    }

    public Address getAddress() {
        return new Address(streetAddress, streetAddress2, "Denver", "Colorado", zipCode);
    }

    public int getLotCapacity() {
        return lotCapacity;
    }

    public String getLotId() {
        return lotId;
    }

    public double getLotPrice() {
        return (chargingMode == ChargingMode.ENTRY_EXIT)
                ? hourlyCents / 100.0   // hourly rate for EXIT/ENTRY lots
                : entryFeeCents / 100.0; // entry fee for ENTRY_ONLY lots
    }
}

