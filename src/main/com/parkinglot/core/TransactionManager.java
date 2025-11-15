package com.parkinglot.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TransactionManager {
    private List<ParkingTransaction> transactions;

    private HashMap<String, List<ParkingTransaction>> vehicleTransaction;

    public TransactionManager()
    {
        this.transactions = new ArrayList<>();
        this.vehicleTransaction = new HashMap<>();
    }

    /**
     * This method will create a parking transaction and will add it to the transactions list.
     */
    public ParkingTransaction park(Calendar date, Permit permit, ParkingLot lot, Car car) {
        ParkingCharge parkingCharge = lot.entry(date.toInstant(), car);
        ParkingTransaction parkingTransaction = new ParkingTransaction(date, permit, lot, parkingCharge.getAmount());
        // Add data to Stored values
        transactions.add(parkingTransaction);

        List<ParkingTransaction> targetList = null;
        for (List<ParkingTransaction> list : vehicleTransaction.values()) {
            if (!list.isEmpty() && permit.permitId().equals(list.getFirst().getPermit().permitId())) {
                targetList = list;
                break;
            }
        }

        if (targetList == null) {
            targetList = new ArrayList<>();
            vehicleTransaction.put(permit.permitId(), targetList);
        }
        targetList.add(parkingTransaction);

        return parkingTransaction;
    }

    /**
     * Get Total in Cents based on permit match
     * @param permit
     * @return Money
     */
    public Money getParkingCharges(Permit permit) {
        long totalAmount = 0;
        for (ParkingTransaction parkingTransaction : transactions) {
            if (parkingTransaction.getPermit().equals(permit)) {
                totalAmount += parkingTransaction.getFeeCharged().getCents();
            }
        }
        return new Money(totalAmount);
    }

    /**
     * Get Total in Cents based on License match
     * @param licensePlate
     * @return Money
     */
    public Money getParkingCharges(String licensePlate) {
        long totalAmount = 0;
        for (ParkingTransaction parkingTransaction : transactions) {
            if (parkingTransaction.getPermit().getLicensePlate().equals(licensePlate)) {
                totalAmount += parkingTransaction.getFeeCharged().getCents();
            }
        }
        return new Money(totalAmount);
    }


}
