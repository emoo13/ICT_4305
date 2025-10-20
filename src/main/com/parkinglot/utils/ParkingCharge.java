package com.parkinglot.utils;

import com.parkinglot.core.Car;
import com.parkinglot.core.Money;
import com.parkinglot.enums.CarType;
import com.parkinglot.enums.ChargingMode;
import com.parkinglot.enums.ParkingLots;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

public class ParkingCharge {
    private final String permitId;
    private final String lotId;
    private static Instant incurred;
    private final Money amount;

    public ParkingCharge(String permitId, String lotId, Instant incurred, Money amount) {
        this.permitId = permitId;
        this.lotId = lotId;
        this.incurred = incurred;
        this.amount = amount;
    }

    public Money getAmount() {
        return amount;
    }

    public String getPermitId() {
        return permitId;
    }

    public String getLotId() {
        return lotId;
    }

    public static ParkingCharge entryCharge(String permitId, String lotId, Instant entryTime, Car car) {
        ParkingLots parkingLot = ParkingLots.valueOf(lotId.replace("-", "").toUpperCase());
        ChargingMode chargeType = parkingLot.getChargingMode();

        if (chargeType.equals(ChargingMode.ENTRY_EXIT)) {
            return new ParkingCharge(permitId, lotId, entryTime, new Money(0));
        }

        long entryCents = calculateDiscount(car, parkingLot.getEntryFee().getCents());
        return new ParkingCharge(permitId, lotId, entryTime, new Money(entryCents));
    }

    public static ParkingCharge exitCharge(String permitId, String lotId, Instant checkoutTime, Car car) {
        ParkingLots parkingLot = ParkingLots.valueOf(lotId.replace("-", "").toUpperCase());
        ChargingMode chargeType = parkingLot.getChargingMode();

        long totalAfterOvernight = 0;
        if (chargeType.equals(ChargingMode.ENTRY_EXIT)) {
            Money hourlyFee  = parkingLot.getHourlyFee();
            // If hourly lot, use hourly time
            // Calculate how many hours parked there
            long minutesParked = Duration.between(incurred, checkoutTime).toMinutes();
            long hoursParked = Math.max(1, (minutesParked + 59) / 60);

            // In a real world scenario, it would charge the user
            totalAfterOvernight = calculateDiscount(car, hoursParked * hourlyFee.getCents());

        } else {
            Money overnightFee = parkingLot.getOvernightFee();
            // If fixed lot, only charge on entry, then overnight
            int overnightParked = Math.toIntExact(
                    java.time.temporal.ChronoUnit.DAYS.between(
                            incurred.atZone(ZoneOffset.UTC).toLocalDate(),
                            checkoutTime.atZone(ZoneOffset.UTC).toLocalDate()
                    )
            );

            totalAfterOvernight = calculateDiscount(car,overnightParked * overnightFee.getCents());

        }

        return new ParkingCharge(permitId, lotId, checkoutTime, new Money(totalAfterOvernight));
    }

    public String toString() {
        return permitId + " " + lotId + " " + incurred + " " + amount;
    }

    /**
     * Determine if car qualifies for 20% discount
     * @param car - Car data based on lot entry/exit
     * @param price - total price after discount
     * @return total price after any applicable discounts
     */
    private static long calculateDiscount(Car car, long price) {
        // Discount is 20%
        if (car.getCarType() == CarType.COMPACT) {
            return (price * 80)/100;
        }
        return price;
    }

}
