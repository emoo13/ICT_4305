package com.parkinglot;

public class Address {
    private final String streetAddress1;
    private final String streetAddress2;
    private final String city;
    private final String state;
    private final String zipCode;

    public Address(String streetAddress1, String streetAddress2,
                   String city, String state, String zipCode) {
        this.streetAddress1 = streetAddress1;
        this.streetAddress2 = streetAddress2;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    // Getters
    public String getStreetAddress1() {
        return streetAddress1;
    }
    public String getStreetAddress2() {
        return streetAddress2;
    }
    public String getCity() {
        return city;
    }
    public String getState() {
        return state;
    }
    public String getZip() {
        return zipCode;
    }

    public String getAddressInfo() {
        return "Street Address 1: " + streetAddress1 + "\n" +
                "Street Address 2: " + streetAddress2 + "\n" +
                "City: " + city + "\n" +
                "State: " + state + "\n" +
                "Zip Code: " + zipCode;
    }
}
