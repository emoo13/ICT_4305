package com.parkinglot.core;

import java.time.Instant;
import java.util.Objects;

public record Permit(String permitId, String lotId, String license, Instant expiration) {
    public Permit {
        Objects.requireNonNull(permitId);
        Objects.requireNonNull(lotId);
        Objects.requireNonNull(license);
        Objects.requireNonNull(expiration);
    }

    //Getters
    public String getLicensePlate() {
        return license;
    }

    public String getPermitId() {
        return permitId;
    }

    public String getLotId() {
        return lotId;
    }

    public Instant getExpiration() {
        return expiration;
    }
}
