package com.parkinglot.core;

import java.util.Calendar;
import java.util.Objects;

public record ParkingTransaction(Calendar transactionDate, Permit permit, ParkingLot lot, Money feeCharged) {
    public ParkingTransaction {
        Objects.requireNonNull(transactionDate);
        Objects.requireNonNull(permit);
        Objects.requireNonNull(lot);
        Objects.requireNonNull(feeCharged);
    }

    public Calendar getTransactionDate() {
        return transactionDate;
    }

    public Permit getPermit() {
        return permit;
    }

    public ParkingLot getLot() {
        return lot;
    }

    public Money getFeeCharged() {
        return feeCharged;
    }

}