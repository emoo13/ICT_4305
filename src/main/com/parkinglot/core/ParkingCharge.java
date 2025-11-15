package com.parkinglot.core;

import com.parkinglot.enums.CarType;
import com.parkinglot.enums.ChargingMode;
import com.parkinglot.enums.ParkingLots;

import java.time.*;
import java.util.Objects;

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
            // If fixed lot, only charge on entry, then overnight starting at 2AM
            LocalDateTime incurredDT  = incurred.atZone(ZoneOffset.UTC).toLocalDateTime();
            LocalDateTime checkoutDT  = checkoutTime.atZone(ZoneOffset.UTC).toLocalDateTime();

            long nights = java.time.temporal.ChronoUnit.DAYS.between(
                    incurredDT.toLocalDate(),
                    checkoutDT.toLocalDate()
            );

            LocalTime cutoff = LocalTime.of(2, 0);
            if (checkoutDT.toLocalTime().isBefore(cutoff)) {
                nights = Math.max(0, nights - 1);
            }

            int overnightParked = Math.toIntExact(nights);

            totalAfterOvernight = calculateDiscount(car,overnightParked * overnightFee.getCents());

        }

        return new ParkingCharge(permitId, lotId, checkoutTime, new Money(totalAfterOvernight));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParkingCharge that)) {
            return false;
        }

        return Objects.equals(this.getPermitId(), that.getPermitId())
                && Objects.equals(this.getLotId(), that.getLotId())
                && Objects.equals(this.getAmount(), that.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPermitId(), getLotId(), getAmount());
    }

    @Override
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
