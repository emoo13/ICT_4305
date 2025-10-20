package com.parkinglot.core;

public class Money {
    private final long cents;

    public Money(long cents) {
        this.cents = cents;
    }

    public double getDollars() {
        return cents/100.0;
    }

    public long getCents() {
        return cents;
    }

    public String toString() {
        return String.valueOf(cents);
    }
}
