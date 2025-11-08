package com.parkinglot.core;

import java.util.Objects;

public class Customer {
    private final String customerId;
    private final String name;
    private final Address address;
    private final String phoneNumber;

    public Customer(String customerId, String name, Address address, String phoneNumber) {
        this.customerId = customerId;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    // Getters
    public String getCustomerId() {
        return customerId;
    }
    public String getCustomerName() {
        return name;
    }
    public Address getCustomerAddress() {
        return address;
    }
    public String getCustomerPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public boolean equals(Object customerId) {
        if (this == customerId) {
            return true;
        }

        if (!(customerId instanceof Customer that)) {
            return false;
        }

        return Objects.equals(this.getCustomerId(), that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    @Override
    public String toString() {
        return "--Customer Data-- \n" +
                "------------------" +
                "Customer ID: " + customerId + "\n" +
                "Customer Name: " + name + "\n" +
                "Address: " + address + "\n" +
                "Phone Number: " + phoneNumber;
    }
}
