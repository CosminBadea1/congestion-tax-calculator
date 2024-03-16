package com.assignment.congestion.tax.calculator.domain.core.model.vehicles;

import java.util.Arrays;

public enum VehicleType {

    CAR, TRACTOR, EMERGENCY, BUS, DIPLOMAT, MOTORCYCLE, MILITARY, FOREIGN;

    private static final String VEHICLE_NOT_SUPPORTED_MSG = "Vehicle of type %s is not supported!";

    public static VehicleType from(String type) {
        return Arrays.stream(values())
                .filter(vehicleType -> vehicleType.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(VEHICLE_NOT_SUPPORTED_MSG.formatted(type)));
    }
}
